package kwasilewski.marketplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.helper.HintSpinner;
import kwasilewski.marketplace.retrofit.listener.AdListener;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.listener.HintListener;
import kwasilewski.marketplace.retrofit.manager.AdManager;
import kwasilewski.marketplace.retrofit.manager.HintManager;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SpinnerUtil;
import kwasilewski.marketplace.util.ValidUtil;
import okhttp3.ResponseBody;

public class EditActivity extends AppCompatActivity implements HintListener, AdListener, ErrorListener {

    private int position;
    private Long adId;

    private AdDetailsData ad;

    private boolean inProgress = false;
    private HintManager hintManager;
    private AdManager adManager;

    private View progressBar;
    private View editForm;
    private TextInputEditText titleField;
    private TextInputEditText priceField;
    private TextInputEditText descriptionField;
    private TextInputEditText cityField;
    private TextInputEditText phoneField;
    private HintSpinner provinceField;
    private HintSpinner categoryField;
    private HintSpinner subcategoryField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_edit);

        adId = extras.getLong(AppConstants.AD_ID_KEY);
        position = extras.getInt(AppConstants.AD_POS_KEY);

        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        hintManager = new HintManager(this, this);
        adManager = new AdManager(this, this);

        progressBar = findViewById(R.id.edit_progress);
        editForm = findViewById(R.id.edit_form);

        titleField = findViewById(R.id.edit_title);
        priceField = findViewById(R.id.edit_price);
        priceField.addTextChangedListener(ValidUtil.positiveNumber());
        descriptionField = findViewById(R.id.edit_description);
        cityField = findViewById(R.id.edit_city);
        phoneField = findViewById(R.id.edit_phone);

        provinceField = findViewById(R.id.edit_province);
        provinceField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, provinceField));

        categoryField = findViewById(R.id.edit_category);
        categoryField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, categoryField, this, subcategoryField));

        subcategoryField = findViewById(R.id.edit_subcategory);
        subcategoryField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, subcategoryField));

        Button saveButton = findViewById(R.id.edit_save_button);
        saveButton.setOnClickListener(v -> attemptModify(ad.isActive()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (subcategoryField.getAdapter() == null) {
            showProgress(true);
            pullAd();
        }
    }

    @Override
    protected void onPause() {
        hintManager.cancelCalls();
        adManager.cancelCalls();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(AppConstants.AD_ID_KEY, adId);
        outState.putInt(AppConstants.AD_POS_KEY, position);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MRKUtil.backButtonClicked(this, item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.LOGIN_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            adManager.updateToken();
            modifyAd();
        }
    }

    private void showProgress(final boolean show) {
        inProgress = show;
        MRKUtil.showProgressBar(this, editForm, progressBar, show);
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

    private void attemptModify(boolean active) {
        if (inProgress) {
            return;
        }

        inProgress = true;

        titleField.setError(null);
        priceField.setError(null);
        categoryField.setError(null);
        subcategoryField.setError(null);
        cityField.setError(null);
        provinceField.setError(null);
        phoneField.setError(null);

        String title = titleField.getText().toString().trim();
        String price = priceField.getText().toString();
        String description = descriptionField.getText().toString();
        String city = cityField.getText().toString().trim();
        String phone = phoneField.getText().toString();
        Long province = provinceField.getItemId();
        Long category = categoryField.getItemId();
        Long subcategory = subcategoryField.getItemId();

        boolean cancel = false;
        View focusView = null;

        if (!ValidUtil.phoneValid(this, phone, phoneField, false)) {
            focusView = phoneField;
            cancel = true;
        }

        if (ValidUtil.spinnerEmpty(this, province, provinceField)) {
            focusView = provinceField;
            cancel = true;
        }

        if (ValidUtil.fieldEmpty(this, city, cityField)) {
            focusView = cityField;
            cancel = true;
        }

        if (ValidUtil.spinnerEmpty(this, category, categoryField)) {
            focusView = categoryField;
            cancel = true;
        } else if (ValidUtil.spinnerEmpty(this, subcategory, subcategoryField)) {
            focusView = subcategoryField;
            cancel = true;
        }

        if (ValidUtil.fieldEmpty(this, price, priceField)) {
            focusView = priceField;
            cancel = true;
        }

        if (ValidUtil.fieldEmpty(this, title, titleField)) {
            focusView = titleField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            inProgress = false;
        } else {
            showProgress(true);
            ad.update(title, price, subcategory, province, description, city, phone, active);
            modifyAd();
        }
    }

    private void modifyAd() {
        if (!inProgress) {
            inProgress = true;
            adManager.modifyAd(adId, ad, this);
        }
    }

    @Override
    public void adReceived(AdDetailsData ad) {
        this.ad = ad;

        initButton();
        initFields();
        hintManager.getAllHints(new ErrorListener() {
        });
    }

    private void initButton() {
        Button activeButton = findViewById(R.id.edit_active_button);
        activeButton.setVisibility(ad.isActive() ? View.GONE : View.VISIBLE);
        if (!ad.isActive()) {
            activeButton.setOnClickListener(v -> attemptModify(true));
        }
    }

    private void initFields() {
        titleField.setText(ad.getTitle());
        priceField.setText(ad.getPrice());
        descriptionField.setText(ad.getDescription());
        cityField.setText(ad.getCity());
        phoneField.setText(ad.getPhone());

        provinceField.setText(ad.getProvince());
        provinceField.setItemId(ad.getPrvId());

        categoryField.setText(ad.getCategory());
        categoryField.setItemId(ad.getCatId());

        subcategoryField.setText(ad.getSubcategory());
        subcategoryField.setItemId(ad.getSctId());
    }

    @Override
    public void hintsReceived(ComboHintData hints) {
        SpinnerUtil.setHintAdapter(this, provinceField, hints.getProvinces());
        SpinnerUtil.setHintAdapter(this, categoryField, hints.getCategories());
        SpinnerUtil.setHintAdapter(this, subcategoryField, hints.getCategories().stream().filter(cat -> cat.getId().equals(categoryField.getItemId())).findAny().orElse(null).getSubcategories());
        showProgress(false);
    }

    @Override
    public void adModified(ResponseBody responseBody) {
        MRKUtil.toast(this, getString(R.string.toast_ad_modified));
        if (ad.isActive()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(AppConstants.AD_POS_KEY, position);
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }

    @Override
    public void notFound(Activity activity) {
        addNotExists();
    }

    @Override
    public void unauthorized(Activity activity) {
        showProgress(false);
        MRKUtil.toast(this, getString(R.string.toast_token_expired));
        startActivityForResult(new Intent(this, LoginActivity.class), AppConstants.LOGIN_CODE);
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        showProgress(false);
        MRKUtil.connectionProblem(this);
    }

    private void addNotExists() {
        MRKUtil.toast(this, getString(R.string.toast_ad_not_available));
        Intent resultIntent = new Intent();
        resultIntent.putExtra(AppConstants.AD_POS_KEY, position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
