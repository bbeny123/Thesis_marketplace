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

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.helper.PhotoAdapter;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.AdService;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewActivity extends AppCompatActivity {

    private final static int NORMAL_MODE = AdFragment.ListModes.NORMAL_MODE;
    private final static int ACTIVE_MODE = AdFragment.ListModes.ACTIVE_MODE;
    private final static int INACTIVE_MODE = AdFragment.ListModes.INACTIVE_MODE;
    public final static String MODE_KEY = "mode";
    public final static String POSITION_KEY = "position";
    private Long adId;
    private int mode;
    private AdDetailsData ad;
    private AdService adService;
    private Call<AdDetailsData> callAd;
    private Call<ResponseBody> callFav;
    private String token;
    private boolean favourite = false;
    private boolean active = false;
    private boolean favouriteActionInProgress = false;
    private int position;

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
    private Button favouriteButton;
    private Button refreshButton;
    private Button statusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            finish();
            return;
        }

        setContentView(R.layout.activity_view);

        adId = extras.getLong(AppConstants.AD_ID_KEY);
        position = extras.getInt(POSITION_KEY);
        mode = extras.getInt(MODE_KEY);


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

        favouriteButton = findViewById(R.id.view_favourite_button);
        refreshButton = findViewById(R.id.view_refresh_button);
        statusButton = findViewById(R.id.view_status_button);

    }

    private void initButtons() {
        if(token == null) {
            statusButton.setVisibility(View.GONE);
            favouriteButton.setVisibility(View.GONE);
            refreshButton.setVisibility(View.GONE);
            return;
        }
        if (mode == ACTIVE_MODE || mode == INACTIVE_MODE) {
            statusButton.setVisibility(View.VISIBLE);
            setStatusButtonText();
        }  else {
            statusButton.setVisibility(View.GONE);
        }
        if (mode == NORMAL_MODE) {
            favouriteButton.setVisibility(View.VISIBLE);
            setFavouriteButtonText();
        } else {
            favouriteButton.setVisibility(View.GONE);
        }
        if(mode == ACTIVE_MODE) {
            refreshButton.setVisibility(View.VISIBLE);
            refreshButton.setEnabled(ad.isRefreshable());
        } else {
            refreshButton.setVisibility(View.GONE);
        }
        setButtonListeners();
    }

    private void setButtonListeners() {
        if (favouriteButton.getVisibility() == View.VISIBLE) {
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favouriteAction();
                }
            });
        }
        if (refreshButton.getVisibility() == View.VISIBLE) {
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshAction();
                }
            });
        }
        if (statusButton.getVisibility() == View.VISIBLE) {
            statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusAction();
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

    private void setFavouriteButtonText() {
        if(favourite) {
            favouriteButton.setText(getString(R.string.action_ad_remove_favourite));
        } else {
            favouriteButton.setText(getString(R.string.action_ad_favourite));
        }
    }

    private void setStatusButtonText() {
        if(active) {
            statusButton.setText(getString(R.string.action_finish));
        } else {
            statusButton.setText(getString(R.string.action_activate));
        }
    }

    private void setAdData() {
        favourite = ad.isFavourite();
        active = ad.isActive();
        initButtons();
        setAdapter();

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
        viewsText.setText(ad.getViews());
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

    private void refreshAction() {
        if(favouriteActionInProgress) {
            return;
        }
        favouriteActionInProgress = true;
        callFav = adService.refreshAd(token, adId);
        callFav.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    refreshActionSuccess();
                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    addNotExists();
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

    private void statusAction() {
        if(favouriteActionInProgress) {
            return;
        }
        favouriteActionInProgress = true;
        callFav = adService.changeUserAdStatus(token, adId);
        callFav.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    statusActionSuccess();
                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    addNotExists();
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
        Intent resultIntent = new Intent();
        resultIntent.putExtra(POSITION_KEY, position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void favouriteActionSuccess() {
        if(!favourite) MRKUtil.toast(this, getString(R.string.toast_added_favourite));
        else MRKUtil.toast(this, getString(R.string.toast_removed_favourite));
        favourite = !favourite;
        setFavouriteButtonText();
    }

    private void refreshActionSuccess() {
        MRKUtil.toast(this, getString(R.string.toast_removed_favourite));
        ad.setRefreshable(!ad.isRefreshable());
        refreshButton.setEnabled(ad.isRefreshable());
    }

    private void statusActionSuccess() {
        if(!active) MRKUtil.toast(this, getString(R.string.toast_ad_activated));
        else MRKUtil.toast(this, getString(R.string.toast_ad_deactivated));
        active = !active;
        setFavouriteButtonText();
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
