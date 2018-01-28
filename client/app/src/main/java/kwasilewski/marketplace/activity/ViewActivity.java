package kwasilewski.marketplace.activity;

import android.annotation.SuppressLint;
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

    private int position;
    private Long adId;

    private boolean inProgress = false;
    private AdManager adManager;
    private AdDetailsData ad;
    private String token;

    private View progressBar;
    private View adForm;
    private TextView priceText;
    private TextView titleText;
    private TextView descriptionText;
    private TextView nameText;
    private TextView locationText;
    private TextView phoneText;
    private TextView emailText;
    private TextView viewsText;
    private Button favouriteButton;
    private Button refreshButton;
    private Button editButton;
    private Button statusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_view);

        adId = extras.getLong(AppConstants.AD_ID_KEY);
        position = extras.getInt(AppConstants.AD_POSITION);

        Toolbar toolbar = findViewById(R.id.ad_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        adManager = new AdManager(this, this);
        token = SharedPrefUtil.getInstance(this).getToken();

        progressBar = findViewById(R.id.ad_progress);
        adForm = findViewById(R.id.ad_form);
        priceText = findViewById(R.id.ad_price);
        titleText = findViewById(R.id.ad_title);
        descriptionText = findViewById(R.id.ad_description);
        nameText = findViewById(R.id.ad_user_name);
        locationText = findViewById(R.id.ad_location);
        phoneText = findViewById(R.id.ad_phone);
        emailText = findViewById(R.id.ad_email);
        viewsText = findViewById(R.id.ad_views);

        favouriteButton = findViewById(R.id.view_favourite_button);
        refreshButton = findViewById(R.id.view_refresh_button);
        editButton = findViewById(R.id.view_edit_button);
        statusButton = findViewById(R.id.view_status_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pullAd();
    }

    @Override
    protected void onPause() {
        adManager.cancelCalls();
        super.onPause();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(AppConstants.AD_ID_KEY, adId);
        outState.putInt(AppConstants.AD_POSITION, position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MRKUtil.backButtonClicked(this, item);
        return super.onOptionsItemSelected(item);
    }

    private void showProgress(final boolean show) {
        inProgress = show;
        MRKUtil.showProgressBarHideView(this, adForm, progressBar, show);
    }

    private void pullAd() {
        showProgress(true);
        adManager.pullAd(adId, new ErrorListener() {
            @Override
            public void notFound(Activity activity) {
                addNotExists();
            }
        });
    }

    private void setPhotoAdapter() {
        if (ad.getDecodedPhotos().size() == 0) {
            return;
        }

        ViewPager viewPager = findViewById(R.id.ad_view_pager);
        viewPager.setLayoutParams(MRKUtil.getPagerLayout(this));

        PhotoAdapter adapter = new PhotoAdapter(getSupportFragmentManager(), ad.getDecodedPhotos());
        viewPager.setAdapter(adapter);

        PageIndicator indicator = (LinePageIndicator) findViewById(R.id.ad_indicator);
        indicator.setViewPager(viewPager);
    }

    private void initButtons() {
        editButton.setVisibility(token != null && ad.isOwner() ? View.VISIBLE : View.GONE);
        statusButton.setVisibility(token != null && ad.isOwner() ? View.VISIBLE : View.GONE);
        refreshButton.setVisibility(token != null && ad.isOwner() ? View.VISIBLE : View.GONE);
        favouriteButton.setVisibility(token != null && !ad.isOwner() ? View.VISIBLE : View.GONE);
        if (token == null) {
            return;
        }
        if (ad.isOwner()) {
            refreshButton.setEnabled(ad.isRefreshable());
            refreshButton.setOnClickListener(v -> refresh());
            editButton.setOnClickListener(v -> edit());
            statusButton.setOnClickListener(v -> changeStatus());
            setStatusButtonText();
        } else {
            favouriteButton.setOnClickListener(v -> favouriteAction());
            setFavouriteButtonText();
        }
    }

    private void favouriteAction() {
        if (!inProgress) {
            inProgress = true;
            if (ad.isFavourite()) {
                adManager.removeFavourite(adId, this);
            } else {
                adManager.addFavourite(adId, this);
            }
        }
    }

    private void refresh() {
        if (!inProgress) {
            inProgress = true;
            adManager.refreshAd(adId, this);
        }
    }

    private void edit() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(AppConstants.AD_ID_KEY, adId);
        intent.putExtra(AppConstants.AD_POSITION, position);
        //      startActivityForResult(intent, AppConstants);
        startActivity(intent);
    }

    private void changeStatus() {
        if (!inProgress) {
            inProgress = true;
            adManager.changeAdStatus(adId, this);
        }
    }

    private void setFavouriteButtonText() {
        favouriteButton.setText(ad.isFavourite() ? getString(R.string.action_ad_remove_favourite) : getString(R.string.action_ad_favourite));
    }

    private void setStatusButtonText() {
        statusButton.setText(ad.isActive() ? getString(R.string.action_finish) : getString(R.string.action_activate));
    }

    @Override
    public void adReceived(AdDetailsData ad) {
        this.ad = ad;

        initButtons();
        setPhotoAdapter();

        titleText.setText(ad.getTitle());
        nameText.setText(ad.getUserName());
        phoneText.setText(ad.getPhone());
        emailText.setText(ad.getEmail());
        viewsText.setText(ad.getViews());
        priceText.setText(String.format(getString(R.string.ad_price_text), ad.getPrice()));
        locationText.setText(String.format(getString(R.string.ad_location_text), ad.getCity(), ad.getProvince()));

        if (TextUtils.isEmpty(ad.getDescription())) {
            View descriptionForm = findViewById(R.id.ad_description_form);
            descriptionForm.setVisibility(View.GONE);
        } else {
            descriptionText.setText(ad.getDescription());
        }

        showProgress(false);
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
        ad.setFavourite(favourite);
        MRKUtil.toast(this, favourite ? getString(R.string.toast_added_favourite) : getString(R.string.toast_removed_favourite));
        setFavouriteButtonText();
        inProgress = false;
    }

    @Override
    public void adRefreshed(ResponseBody responseBody) {
        ad.setRefreshable(false);
        MRKUtil.toast(this, getString(R.string.toast_ad_refreshed));
        refreshButton.setEnabled(false);
        inProgress = false;
    }

    @Override
    public void adStatusChanged(ResponseBody responseBody) {
        ad.setActive(!ad.isActive());
        MRKUtil.toast(this, ad.isActive() ? getString(R.string.toast_ad_activated) : getString(R.string.toast_ad_deactivated));
        setStatusButtonText();
        inProgress = false;
    }

    @Override
    public void notFound(Activity activity) {
        addNotExists();
    }

    @Override
    public void notAcceptable(Activity activity) {
        MRKUtil.toast(this, ad.isFavourite() ? getString(R.string.toast_already_favourite) : getString(R.string.toast_not_favourite));
        inProgress = false;
    }

    @Override
    public void unauthorized(Activity activity) {
        MRKUtil.toast(this, getString(R.string.toast_own_add_favourite));
        inProgress = false;
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        MRKUtil.connectionProblem(this);
        inProgress = false;
    }

    private void addNotExists() {
        MRKUtil.toast(this, getString(R.string.toast_ad_not_available));
        Intent resultIntent = new Intent();
        resultIntent.putExtra(AppConstants.AD_POSITION, position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
