package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.helper.HintSpinner;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.listener.HintListener;
import kwasilewski.marketplace.retrofit.manager.HintManager;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SpinnerUtil;
import kwasilewski.marketplace.util.ValidUtil;

public class FilterActivity extends AppCompatActivity implements HintListener, ErrorListener {

    private HintManager hintManager;

    private View progressBar;
    private View filterView;
    private TextInputEditText titleField;
    private TextInputEditText priceFromField;
    private TextInputEditText priceToField;
    private HintSpinner provinceField;
    private HintSpinner categoryField;
    private HintSpinner subcategoryField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = findViewById(R.id.filter_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        hintManager = new HintManager(this, this);

        progressBar = findViewById(R.id.filter_progress);
        filterView = findViewById(R.id.filter_form);
        titleField = findViewById(R.id.filter_title);
        priceFromField = findViewById(R.id.filter_price_min);
        priceToField = findViewById(R.id.filter_price_max);
        priceToField.addTextChangedListener(ValidUtil.positiveNumber());

        provinceField = findViewById(R.id.filter_province);
        provinceField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, provinceField));

        categoryField = findViewById(R.id.filter_category);
        categoryField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, categoryField, this, subcategoryField, true));

        subcategoryField = findViewById(R.id.filter_subcategory);
        subcategoryField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, subcategoryField));
        SpinnerUtil.disableSpinner(subcategoryField);

        Button searchButton = findViewById(R.id.filter_button_search);
        searchButton.setOnClickListener(v -> searchAds());

        setDataFromBundles(getIntent().getExtras());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress(true);
        hintManager.getAllHints(this);
    }

    @Override
    protected void onPause() {
        hintManager.cancelCalls();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MRKUtil.backButtonClicked(this, item);
        if (item.getItemId() == R.id.menu_filter_reset) {
            resetFilters();
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchAds() {


        String priceMin = priceFromField.getText().toString();
        String priceMax = priceToField.getText().toString();
        if (MRKUtil.compareNumericStrings(priceMin, priceMax)) {
            String temp = priceMin;
            priceMin = priceMax;
            priceMax = temp;
        }
        Intent result = MRKUtil.getFilterIntent(this, titleField.getText().toString(), priceMin, priceMax, provinceField.getItemId(), categoryField.getItemId(), subcategoryField.getItemId());
        setResult(AppCompatActivity.RESULT_OK, result);
        finish();
    }

    private void setDataFromBundles(Bundle extras) {
        if (extras != null) {
            titleField.setText(extras.getString(AppConstants.TITLE_KEY));
            priceFromField.setText(extras.getString(AppConstants.PRICE_MIN_KEY));
            priceToField.setText(extras.getString(AppConstants.PRICE_MAX_KEY));
            provinceField.setItemId(extras.getLong(AppConstants.PROVINCE_KEY));
            categoryField.setItemId(extras.getLong(AppConstants.CATEGORY_KEY));
            subcategoryField.setItemId(extras.getLong(AppConstants.SUBCATEGORY_KEY));
        }
    }

    private void resetFilters() {
        titleField.setText(null);
        priceFromField.setText(null);
        priceToField.setText(null);
        provinceField.setText(null);
        provinceField.setItemId(null);
        categoryField.setText(null);
        categoryField.setItemId(null);
        subcategoryField.setText(null);
        subcategoryField.setItemId(null);
        SpinnerUtil.disableSpinner(subcategoryField);
        filterView.clearFocus();
        MRKUtil.hideKeyboard(this);
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBar(this, filterView, progressBar, show);
    }

    @Override
    public void hintsReceived(ComboHintData hints) {
        SpinnerUtil.setSpinnerAdapterAndName(this, provinceField, hints.getProvinces());
        SpinnerUtil.setSpinnerAdapterAndName(this, categoryField, hints.getCategories());
        hints.getCategories().stream().filter(cat -> cat.getId().equals(categoryField.getItemId())).findAny().ifPresent(cat -> SpinnerUtil.setSpinnerAdapterAndName(this, subcategoryField, cat.getSubcategories()));
        SpinnerUtil.enableSpinner(subcategoryField, subcategoryField.getAdapter() != null);
        showProgress(false);
    }

}
