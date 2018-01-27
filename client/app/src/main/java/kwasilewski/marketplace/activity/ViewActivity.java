package kwasilewski.marketplace.activity;

import android.app.Activity;
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

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.helper.PhotoAdapter;
import kwasilewski.marketplace.retrofit.listener.AdListener;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.manager.AdManager;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;

public class ViewActivity extends AppCompatActivity implements AdListener, ErrorListener {

    public final static String MODE_KEY = "mode";
    public final static String POSITION_KEY = "position";
    private final static int NORMAL_MODE = AdFragment.ListModes.NORMAL_MODE;
    private final static int ACTIVE_MODE = AdFragment.ListModes.ACTIVE_MODE;
    private final static int INACTIVE_MODE = AdFragment.ListModes.INACTIVE_MODE;
    private Long adId;
    private int mode;
    private AdDetailsData ad;

    private boolean inProgress = false;
    private AdManager adManager;

    private String token;
    private boolean favourite = false;
    private boolean active = false;
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

        adManager = new AdManager(this, this);
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
            favouriteButton.setOnClickListener(v -> favouriteAction());
        }
        if (refreshButton.getVisibility() == View.VISIBLE) {
            refreshButton.setOnClickListener(v -> adManager.refreshAd(adId, this));
        }
        if (statusButton.getVisibility() == View.VISIBLE) {
            statusButton.setOnClickListener(v -> adManager.changeAdStatus(adId, this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ad == null) {
            showProgress(true);
            adManager.getAd(adId, new ErrorListener() {
                @Override
                public void unauthorized(Activity activity) {
                    addNotExists();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        adManager.cancelCalls();
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
        setPhotoAdapter();

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

    private void setPhotoAdapter() {
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

    @Override
    public void adReceived(AdDetailsData ad) {
        this.ad = ad;
        setAdData();
    }

    @Override
    public void notFound(Activity activity) {
        addNotExists();
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        MRKUtil.connectionProblem(this);
    }

    @Override
    public void favouriteAdded(ResponseBody responseBody) {
        favouriteActionSuccess(true);
    }

    @Override
    public void favouriteRemoved(ResponseBody responseBody) {
        favouriteActionSuccess(false);
    }

    private void favouriteActionSuccess(boolean favourite) {
        if (favourite) MRKUtil.toast(this, getString(R.string.toast_added_favourite));
        else MRKUtil.toast(this, getString(R.string.toast_removed_favourite));
        this.favourite = favourite;
        setFavouriteButtonText();
    }

    @Override
    public void notAcceptable(Activity activity) {
        if (favourite) MRKUtil.toast(this, getString(R.string.toast_already_favourite));
        else MRKUtil.toast(this, getString(R.string.toast_not_favourite));
    }

    @Override
    public void unauthorized(Activity activity) {
        MRKUtil.toast(this, getString(R.string.toast_own_add_favourite));
    }

    private void favouriteAction() {
        if (favourite) {
            adManager.removeFavourite(adId, this);
        } else {
            adManager.addFavourite(adId, this);
        }
    }

    @Override
    public void adRefreshed(ResponseBody responseBody) {
        MRKUtil.toast(this, getString(R.string.toast_removed_favourite));
        ad.setRefreshable(!ad.isRefreshable());
        refreshButton.setEnabled(ad.isRefreshable());
    }

    @Override
    public void adStatusChanged(ResponseBody responseBody) {
        if (!active) MRKUtil.toast(this, getString(R.string.toast_ad_activated));
        else MRKUtil.toast(this, getString(R.string.toast_ad_deactivated));
        active = !active;
        setFavouriteButtonText();
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

}
