package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;
import java.util.regex.Pattern;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.ComboHintData;
import kwasilewski.marketplace.dto.HintData;
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

    private HintService hintService;
    private Call<ComboHintData> callHint;
    private Long selectedProvince;
    private Long selectedCategory;
    private Long selectedSubcategory;
    private Long selectedProvinceBundle = 0L;
    private Long selectedCategoryBundle = 0L;
    private Long selectedSubcategoryBundle = 0L;
    private boolean spinnersSettingInProgress = false;

    private View progressBar;
    private View filterView;
    private EditText title;
    private EditText priceFrom;
    private EditText priceTo;
    private HintSpinner categorySpinner;
    private HintSpinner subcategorySpinner;
    private HintSpinner provinceSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = findViewById(R.id.filter_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        hintService = RetrofitService.getInstance().getHintService();

        progressBar = findViewById(R.id.filter_progress_bar);
        filterView = findViewById(R.id.filter_view);
        title = findViewById(R.id.filter_title);
        priceFrom = findViewById(R.id.filter_price_from);
        priceTo = findViewById(R.id.filter_price_to);
        priceTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int length = text.length();
                if(length > 0 && !Pattern.matches("^[1-9][0-9]*$", text)) {
                    s.delete(length - 1, length);
                }
            }
        });

        categorySpinner = findViewById(R.id.filter_category);
        categorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCategory = spinnerOnClickListener(categorySpinner, adapterView.getItemAtPosition(position));
            }
        });

        subcategorySpinner = findViewById(R.id.filter_subcategory);
        enableSubcategorySpinner(false);
        subcategorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedSubcategory = spinnerOnClickListener(subcategorySpinner, adapterView.getItemAtPosition(position));
            }
        });

        provinceSpinner = findViewById(R.id.filter_province);
        provinceSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedProvince = spinnerOnClickListener(provinceSpinner, adapterView.getItemAtPosition(position));
            }
        });

        Button searchButton = findViewById(R.id.filter_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAds();
            }
        });

        setDataFromBundles(getIntent().getExtras());
    }

    private void searchAds() {
        Intent result = new Intent();
        String priceMin = priceFrom.getText().toString();
        String priceMax = priceTo.getText().toString();
        if(swapPrices(priceMin, priceMax)) {
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

    private boolean swapPrices(String priceMin, String priceMax) {
        return !priceMin.isEmpty() && !priceMax.isEmpty() && Long.parseLong(priceMin) > Long.parseLong(priceMax);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpSpinners();
    }

    @Override
    protected void onPause() {
        if (callHint != null) callHint.cancel();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDataFromBundles(Bundle extras) {
        if(extras == null) {
            return;
        }
        title.setText(extras.getString(TITLE_KEY));
        priceFrom.setText(extras.getString(PRICE_FROM_KEY));
        priceTo.setText(extras.getString(PRICE_TO_KEY));
        selectedCategoryBundle = extras.getLong(CATEGORY_KEY);
        selectedSubcategoryBundle = extras.getLong(SUBCATEGORY_KEY);
        selectedProvinceBundle = extras.getLong(PROVINCE_KEY);
    }

    private Long spinnerOnClickListener(HintSpinner spinner, Object item) {
        Long id = null;
        if (item instanceof HintData) {
            id = ((HintData) item).getId();
        }
        if (item instanceof CategoryData && !id.equals(selectedCategory)) {
            setSubcategoryAdapter(((CategoryData) item).getSubcategories());
        }
        spinner.setError(null);
        return id;
    }

    private void enableSubcategorySpinner(boolean enabled) {
        subcategorySpinner.clearFocus();
        subcategorySpinner.setFocusable(enabled);
        subcategorySpinner.setEnabled(enabled);
        subcategorySpinner.setClickable(enabled);
    }

    private void setUpSpinners() {
        if(spinnersSettingInProgress) {
            return;
        }
        spinnersSettingInProgress = true;
        if (provinceSpinner.getAdapter() == null || categorySpinner.getAdapter() == null) {
            showProgress(true);
            populateSpinners();
        }
    }

    private void setProvinceAdapter(List<HintData> hintData) {
        if(selectedProvinceBundle != 0) {
            for (HintData hint : hintData) {
                if (hint.getId().equals(selectedProvinceBundle)) {
                    provinceSpinner.setText(hint.getName());
                    selectedProvince = selectedProvinceBundle;
                    break;
                }
            }
        }
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, hintData);
        provinceSpinner.setAdapter(adapter);
    }

    private void setCategoryAdapter(List<CategoryData> categoryData) {
        if(selectedCategoryBundle != 0) {
            for (CategoryData category : categoryData) {
                if (category.getId().equals(selectedCategoryBundle)) {
                    categorySpinner.setText(category.getName());
                    selectedCategory = selectedCategoryBundle;
                    setSubcategoryAdapter(category.getSubcategories());
                    break;
                }
            }
        }
        ArrayAdapter<CategoryData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, categoryData);
        categorySpinner.setAdapter(adapter);
    }

    private void setSubcategoryAdapter(List<HintData> hintData) {
        selectedSubcategory = null;
        subcategorySpinner.setText(null);
        if(selectedSubcategoryBundle != 0) {
            for (HintData hint : hintData) {
                if (hint.getId().equals(selectedSubcategoryBundle)) {
                    subcategorySpinner.setText(hint.getName());
                    selectedSubcategory = selectedSubcategoryBundle;
                    break;
                }
            }
        }
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, hintData);
        subcategorySpinner.setAdapter(adapter);
        enableSubcategorySpinner(true);
    }

    private void setAdapters(ComboHintData hints) {
        setCategoryAdapter(hints.getCategories());
        setProvinceAdapter(hints.getProvinces());
        showProgress(false);
        spinnersSettingInProgress = false;
    }

    private void populateSpinners() {
        callHint = hintService.getAllHints();
        callHint.enqueue(new Callback<ComboHintData>() {
            @Override
            public void onResponse(Call<ComboHintData> call, Response<ComboHintData> response) {
                if (response.isSuccessful()) {
                    setAdapters(response.body());
                } else {
                    connectionProblemAtStart();
                }
            }

            @Override
            public void onFailure(Call<ComboHintData> call, Throwable t) {
                if (!call.isCanceled()) connectionProblemAtStart();
            }
        });
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBarHideView(this, filterView, progressBar, show);
    }

    private void connectionProblemAtStart() {
        startActivity(new Intent(this, NetErrorActivity.class));
    }

}
