package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

import java.net.HttpURLConnection;
import java.util.Locale;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.helper.PhotoAdapter;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.AdService;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdActivity extends AppCompatActivity {

    private Long adId;
    private AdDetailsData ad;
    private AdService adService;
    private Call<AdDetailsData> callAd;
    private Call<ResponseBody> callFav;
    private String token;
    private boolean favourite = false;
    private boolean favouriteActionInProgress = false;

    private View progressBar;
    private View adFormView;
    private TextView priceText;
    private TextView titleText;
    private TextView descriptionText;
    private TextView sellerNameText;
    private TextView sellerLocationText;
    private TextView phoneText;
    private TextView emailText;
    private TextView viewsText;
    private Button adButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            finish();
            return;
        }

        setContentView(R.layout.activity_ad);

        adId = extras.getLong(AppConstants.AD_ID_KEY);

        Toolbar toolbar = findViewById(R.id.ad_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        adService = RetrofitService.getInstance().getAdService();
        token = SharedPrefUtil.getInstance(this).getToken();

        progressBar = findViewById(R.id.ad_progress);
        adFormView = findViewById(R.id.ad_form);
        priceText = findViewById(R.id.ad_price);
        titleText = findViewById(R.id.ad_title);
        descriptionText = findViewById(R.id.ad_description);
        sellerNameText = findViewById(R.id.ad_user_name);
        sellerLocationText = findViewById(R.id.ad_location);
        phoneText = findViewById(R.id.ad_phone);
        emailText = findViewById(R.id.ad_email);
        viewsText = findViewById(R.id.ad_views);

        adButton = findViewById(R.id.ad_button);
        if (token == null) {
            adButton.setVisibility(View.GONE);
        } else {
            adButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favouriteAction();
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ad == null) {
            showProgress(true);
            initAd();
        }
    }

    @Override
    protected void onPause() {
        if(callAd != null) callAd.cancel();
        if(callFav != null) callFav.cancel();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(AppConstants.AD_ID_KEY, adId);
    }

    private void setButtonText() {
        if(favourite) {
            adButton.setText(getString(R.string.action_ad_remove_favourite));
        } else {
            adButton.setText(getString(R.string.action_ad_favourite));
        }
    }

    private void setAdData() {
        setAdapter();
        favourite = ad.isFavourite();
        if(adButton.getVisibility() == View.VISIBLE) {
            setButtonText();
        }

        priceText.setText(String.format(getString(R.string.ad_price_text), ad.getPrice()));
        titleText.setText(ad.getTitle());
        if(TextUtils.isEmpty(ad.getDescription())) {
            View descriptionForm = findViewById(R.id.ad_description_form);
            descriptionForm.setVisibility(View.GONE);
        } else {
            descriptionText.setText(ad.getDescription());
        }
        sellerNameText.setText(ad.getUserName());
        sellerLocationText.setText(String.format(getString(R.string.ad_location_text), ad.getCity(), ad.getProvince()));
        phoneText.setText(ad.getPhone());
        emailText.setText(ad.getEmail());
        viewsText.setText(String.format(Locale.getDefault(), "%d", ad.getViews()));
        showProgress(false);
    }

    private void setAdapter() {
        if (ad.getPhotos().size() == 0) {
            return;
        }

        ViewPager viewPager = findViewById(R.id.ad_view_pager);
        viewPager.setLayoutParams(MRKUtil.getPagerLayout(this));

        PhotoAdapter adapter = new PhotoAdapter(getSupportFragmentManager(), ad.getPhotos());
        viewPager.setAdapter(adapter);

        PageIndicator indicator = (LinePageIndicator)findViewById(R.id.ad_indicator);
        indicator.setViewPager(viewPager);
    }

    private void initAd() {
        callAd = token != null ? adService.getUserAd(token, adId) : adService.getAd(adId);
        callAd.enqueue(new Callback<AdDetailsData>() {
            @Override
            public void onResponse(Call<AdDetailsData> call, Response<AdDetailsData> response) {
                if (response.isSuccessful()) {
                    ad = response.body();
                    setAdData();
                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    addNotExists();
                } else {
                    connectionProblemAtStart();
                }
            }

            @Override
            public void onFailure(Call<AdDetailsData> call, Throwable t) {
                if (!call.isCanceled()) connectionProblemAtStart();
            }
        });
    }

    private void favouriteAction() {
        if(favouriteActionInProgress) {
            return;
        }
        favouriteActionInProgress = true;
        callFav = favourite ? adService.removeFavourite(token, adId) : adService.addFavourite(token, adId);
        callFav.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    favouriteActionSuccess();
                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    addNotExists();
                } else if (response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                    favouriteActionNotAcceptable();
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    ownAd();
                } else {
                    connectionProblem();
                }
                favouriteActionInProgress = false;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!call.isCanceled()) connectionProblem();
                favouriteActionInProgress = false;
            }
        });
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBarHideView(this, adFormView, progressBar, show);
    }

    private void addNotExists() {
        MRKUtil.toast(this, getString(R.string.toast_ad_not_available));
        //TODO: remove add from list
        finish();
    }

    private void favouriteActionSuccess() {
        if(!favourite) MRKUtil.toast(this, getString(R.string.toast_added_favourite));
        else MRKUtil.toast(this, getString(R.string.toast_removed_favourite));
        favourite = !favourite;
        setButtonText();
    }

    private void favouriteActionNotAcceptable() {
        if(favourite) MRKUtil.toast(this, getString(R.string.toast_already_favourite));
        else MRKUtil.toast(this, getString(R.string.toast_not_favourite));

    }

    private void ownAd() {
        MRKUtil.toast(this, getString(R.string.toast_own_add_favourite));
    }

    private void connectionProblem() {
        MRKUtil.connectionProblem(this);
    }

    private void connectionProblemAtStart() {
        startActivity(new Intent(this, NetErrorActivity.class));
    }



}
