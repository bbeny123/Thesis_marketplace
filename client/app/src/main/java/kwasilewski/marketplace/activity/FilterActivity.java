package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.hint.CategoryData;
import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.helper.HintSpinner;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.HintService;
import kwasilewski.marketplace.util.MRKUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterActivity extends AppCompatActivity {

    public static final String TITLE_KEY = "title";
    public static final String PRICE_FROM_KEY = "priceFrom";
    public static final String PRICE_TO_KEY = "priceTo";
    public static final String CATEGORY_KEY = "categoryId";
    public static final String SUBCATEGORY_KEY = "subcategoryId";
    public static final String PROVINCE_KEY = "provinceId";

    private final HintService hintService = RetrofitService.getInstance().getHintService();
    private Call<ComboHintData> callHint;
    private final Callback<ComboHintData> callbackHint = new Callback<ComboHintData>() {
        @Override
        public void onResponse(Call<ComboHintData> call, Response<ComboHintData> response) {
            if (response.isSuccessful()) {
                setSpinnersAdapter(response.body());
            } else {
                connectionProblemAtStart();
            }
        }

        @Override
        public void onFailure(Call<ComboHintData> call, Throwable t) {
            if (!call.isCanceled()) connectionProblemAtStart();
        }
    };
    private Long selectedProvince;
    private Long selectedCategory;
    private Long selectedSubcategory;
    private Long provinceBundle = 0L;
    private Long categoryBundle = 0L;
    private Long subcategoryBundle = 0L;
    private boolean settingSpinners = false;

    private final AdapterView.OnItemClickListener listenerProvince = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            selectedProvince = spinnerOnClickListener(adapterView.getItemAtPosition(position));
        }
    };
    private final AdapterView.OnItemClickListener listenerCategory = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            selectedCategory = spinnerOnClickListener(adapterView.getItemAtPosition(position));
        }
    };
    private final AdapterView.OnItemClickListener listenerSubcategory = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            selectedSubcategory = spinnerOnClickListener(adapterView.getItemAtPosition(position));
        }
    };
    private final View.OnClickListener listenerSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchAds();
        }
    };

    private View progressBar;
    private View filterView;
    private TextInputEditText title;
    private TextInputEditText priceFrom;
    private TextInputEditText priceTo;
    private HintSpinner provinceSpinner;
    private HintSpinner categorySpinner;
    private HintSpinner subcategorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = findViewById(R.id.filter_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        progressBar = findViewById(R.id.filter_progress_bar);
        filterView = findViewById(R.id.filter_view);
        title = findViewById(R.id.filter_title);
        priceFrom = findViewById(R.id.filter_price_from);
        priceTo = findViewById(R.id.filter_price_to);
        priceTo.addTextChangedListener(MRKUtil.getTextWatcherPositiveNumber());

        provinceSpinner = findViewById(R.id.filter_province);
        provinceSpinner.setOnItemClickListener(listenerProvince);
        categorySpinner = findViewById(R.id.filter_category);
        categorySpinner.setOnItemClickListener(listenerCategory);
        subcategorySpinner = findViewById(R.id.filter_subcategory);
        subcategorySpinner.setOnItemClickListener(listenerSubcategory);
        enableSubcategorySpinner(false);

        Button searchButton = findViewById(R.id.filter_search_button);
        searchButton.setOnClickListener(listenerSearch);

        setDataFromBundles(getIntent().getExtras());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.filters_reset) {
            resetFilters();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareSpinners();
    }

    @Override
    protected void onPause() {
        if (callHint != null) callHint.cancel();
        super.onPause();
    }

    private void setDataFromBundles(Bundle extras) {
        if (extras == null) {
            return;
        }
        title.setText(extras.getString(TITLE_KEY));
        priceFrom.setText(extras.getString(PRICE_FROM_KEY));
        priceTo.setText(extras.getString(PRICE_TO_KEY));
        categoryBundle = extras.getLong(CATEGORY_KEY);
        subcategoryBundle = extras.getLong(SUBCATEGORY_KEY);
        provinceBundle = extras.getLong(PROVINCE_KEY);
    }

    private void resetFilters() {
        title.setText(null);
        priceFrom.setText(null);
        priceTo.setText(null);
        selectedProvince = null;
        selectedCategory = null;
        selectedSubcategory = null;
        provinceSpinner.setText(null);
        categorySpinner.setText(null);
        subcategorySpinner.setText(null);
        filterView.clearFocus();
        MRKUtil.hideKeyboard(this);
        enableSubcategorySpinner(false);
    }

    private Long spinnerOnClickListener(Object item) {
        Long id = null;
        if (item instanceof HintData) {
            id = ((HintData) item).getId();
        }
        if (item instanceof CategoryData && !id.equals(selectedCategory)) {
            setSubcategoryAdapter(((CategoryData) item).getSubcategories());
        }
        return id;
    }

    private void enableSubcategorySpinner(boolean enabled) {
        subcategorySpinner.clearFocus();
        subcategorySpinner.setFocusable(enabled);
        subcategorySpinner.setEnabled(enabled);
        subcategorySpinner.setClickable(enabled);
    }

    private void prepareSpinners() {
        if (settingSpinners) {
            return;
        }
        settingSpinners = true;
        if (provinceSpinner.getAdapter() == null || categorySpinner.getAdapter() == null) {
            showProgress(true);
            pullHints();
        }
    }

    private void pullHints() {
        callHint = hintService.getAllHints();
        callHint.enqueue(callbackHint);
    }

    private void setSpinnersAdapter(ComboHintData hints) {
        setProvinceAdapter(hints.getProvinces());
        setCategoryAdapter(hints.getCategories());

        showProgress(false);
        settingSpinners = false;
    }

    private void setProvinceAdapter(List<HintData> hintData) {
        if (checkBundleSpinner(provinceBundle, hintData, provinceSpinner)) {
            selectedProvince = provinceBundle;
        }
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, hintData);
        provinceSpinner.setAdapter(adapter);
    }

    private void setCategoryAdapter(List<CategoryData> categoryData) {
        if (categoryBundle != 0) {
            for (CategoryData category : categoryData) {
                if (category.getId().equals(categoryBundle)) {
                    categorySpinner.setText(category.getName());
                    selectedCategory = categoryBundle;
                    setSubcategoryAdapter(category.getSubcategories());
                    break;
                }
            }
        }
        ArrayAdapter<CategoryData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, categoryData);
        categorySpinner.setAdapter(adapter);
    }

    private void setSubcategoryAdapter(List<HintData> hintData) {
        subcategorySpinner.setText(null);
        selectedSubcategory = checkBundleSpinner(subcategoryBundle, hintData, subcategorySpinner) ? subcategoryBundle : null;
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, hintData);
        subcategorySpinner.setAdapter(adapter);
        enableSubcategorySpinner(true);
    }

    private boolean checkBundleSpinner(Long bundleValue, List<HintData> hintData, HintSpinner spinner) {
        if (bundleValue != 0) {
            for (HintData hint : hintData) {
                if (hint.getId().equals(bundleValue)) {
                    spinner.setText(hint.getName());
                    return true;
                }
            }
        }
        return false;
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBarHideView(this, filterView, progressBar, show);
    }

    private void connectionProblemAtStart() {
        startActivity(new Intent(this, NetErrorActivity.class));
    }

    private void searchAds() {
        Intent result = new Intent();
        String priceMin = priceFrom.getText().toString();
        String priceMax = priceTo.getText().toString();
        if (MRKUtil.compareNumericStrings(priceMin, priceMax)) {
            String temp = priceMin;
            priceMin = priceMax;
            priceMax = temp;
        }
        result.putExtra(TITLE_KEY, title.getText().toString());
        result.putExtra(PRICE_FROM_KEY, priceMin);
        result.putExtra(PRICE_TO_KEY, priceMax);
        result.putExtra(SUBCATEGORY_KEY, selectedSubcategory);
        result.putExtra(CATEGORY_KEY, selectedCategory);
        result.putExtra(PROVINCE_KEY, selectedProvince);
        setResult(AppCompatActivity.RESULT_OK, result);
        finish();
    }

}
