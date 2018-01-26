package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.net.HttpURLConnection;
import java.util.List;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdData;
import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.dto.hint.CategoryData;
import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.helper.HintSpinner;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.AdService;
import kwasilewski.marketplace.retrofit.service.HintService;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditActivity extends AppCompatActivity {

    public final static String POSITION_KEY = "position";
    private final int LOGIN_CODE = 1;
    private final HintService hintService = RetrofitService.getInstance().getHintService();
    private int position;
    private Call<ComboHintData> callHint;
    private Long adId;
    private AdDetailsData ad;
    private AdService adService;
    private Call<AdDetailsData> callAd;
    private Call<ResponseBody> callModifyAd;
    private String token;
    private Long selectedProvince;
    private Long selectedCategory;
    private Long selectedSubcategory;
    private boolean init = false;
    private boolean addInProgress = false;
    private View progressBar;
    private View editForm;
    private TextInputEditText title;
    private TextInputEditText price;
    private TextInputEditText description;
    private TextInputEditText city;
    private TextInputEditText phone;
    private HintSpinner provinceSpinner;
    private HintSpinner categorySpinner;
    private HintSpinner subcategorySpinner;
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
    private final AdapterView.OnItemClickListener listenerProvince = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            selectedProvince = spinnerOnClickListener(provinceSpinner, adapterView.getItemAtPosition(position));
        }
    };
    private final AdapterView.OnItemClickListener listenerCategory = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            selectedCategory = spinnerOnClickListener(categorySpinner, adapterView.getItemAtPosition(position));
        }
    };
    private final AdapterView.OnItemClickListener listenerSubcategory = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            selectedSubcategory = spinnerOnClickListener(subcategorySpinner, adapterView.getItemAtPosition(position));
        }
    };
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            finish();
            return;
        }

        setContentView(R.layout.activity_edit);

        adId = extras.getLong(AppConstants.AD_ID_KEY);
        position = extras.getInt(POSITION_KEY);

        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        adService = RetrofitService.getInstance().getAdService();
        token = SharedPrefUtil.getInstance(this).getToken();

        progressBar = findViewById(R.id.edit_progress);
        editForm = findViewById(R.id.edit_form);
        title = findViewById(R.id.edit_title);
        price = findViewById(R.id.edit_price);
        price.addTextChangedListener(MRKUtil.getTextWatcherPositiveNumber());
        description = findViewById(R.id.edit_description);
        city = findViewById(R.id.edit_city);
        phone = findViewById(R.id.edit_phone);
        provinceSpinner = findViewById(R.id.edit_province);
        provinceSpinner.setOnItemClickListener(listenerProvince);
        categorySpinner = findViewById(R.id.edit_category);
        categorySpinner.setOnItemClickListener(listenerCategory);
        subcategorySpinner = findViewById(R.id.edit_subcategory);
        subcategorySpinner.setOnItemClickListener(listenerSubcategory);
        saveButton = findViewById(R.id.edit_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAdd(ad.isActive());
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        if(!init) {
            showProgress(true);
            initAd();
        }
    }

    @Override
    protected void onPause() {
        if (callAd != null) callAd.cancel();
        if (callHint != null) callHint.cancel();
        if (callModifyAd != null) callModifyAd.cancel();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            token = SharedPrefUtil.getInstance(this).getToken();
            modifyAd(ad);
        }
    }

    private Long spinnerOnClickListener(HintSpinner spinner, Object item) {
        Long id = null;
        if (item instanceof HintData) {
            id = ((HintData) item).getId();
        }
        if (item instanceof CategoryData && !id.equals(selectedCategory)) {
            resetSubcategoryAdapter(((CategoryData) item).getSubcategories());
        }
        spinner.setError(null);
        return id;
    }

    private void prepareSpinners() {
        if (provinceSpinner.getAdapter() == null || categorySpinner.getAdapter() == null) {
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
        init = true;
        showProgress(false);
    }

    private void setProvinceAdapter(List<HintData> hintData) {
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, hintData);
        provinceSpinner.setAdapter(adapter);
    }

    private void setCategoryAdapter(List<CategoryData> categoryData) {
        if(subcategorySpinner != null) {
            for (CategoryData category : categoryData) {
                if (category.getId().equals(selectedCategory)) {
                    setSubcategoryAdapter(category.getSubcategories());
                    break;
                }
            }
        }
        ArrayAdapter<CategoryData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, categoryData);
        categorySpinner.setAdapter(adapter);
    }

    private void setSubcategoryAdapter(List<HintData> hintData) {
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, hintData);
        subcategorySpinner.setAdapter(adapter);
        enableSubcategorySpinner(true);
    }

    private void resetSubcategoryAdapter(List<HintData> hintData) {
        selectedSubcategory = null;
        subcategorySpinner.setText(null);
        setSubcategoryAdapter(hintData);
    }

    private void enableSubcategorySpinner(boolean enabled) {
        subcategorySpinner.clearFocus();
        subcategorySpinner.setFocusable(enabled);
        subcategorySpinner.setEnabled(enabled);
        subcategorySpinner.setClickable(enabled);
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBarHideView(this, editForm, progressBar, show);
    }

    private void connectionProblemAtStart() {
        startActivity(new Intent(this, NetErrorActivity.class));
    }

    private void initAd() {
        callAd = adService.getUserAd(token, adId);
        callAd.enqueue(new Callback<AdDetailsData>() {
            @Override
            public void onResponse(Call<AdDetailsData> call, Response<AdDetailsData> response) {
                if (response.isSuccessful()) {
                    ad = response.body();
                    init();
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

    private void addNotExists() {
        MRKUtil.toast(this, getString(R.string.toast_ad_not_available));
        Intent resultIntent = new Intent();
        resultIntent.putExtra(POSITION_KEY, position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void init() {
        initButton();
        initAdData();
        initSpinners();
    }

    private void initButton() {
        Button activeButton = findViewById(R.id.edit_active_button);
        activeButton.setVisibility(ad.isActive() ? View.GONE : View.VISIBLE);
        if (!ad.isActive()) {
            activeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attemptAdd(!ad.isActive());
                }
            });
        }
    }

    private void initAdData() {
        title.setText(ad.getTitle());
        price.setText(ad.getPrice());
        description.setText(ad.getDescription());
        city.setText(ad.getCity());
        phone.setText(ad.getPhone());
    }

    private void initSpinners() {
        selectedProvince = ad.getPrvId();
        provinceSpinner.setText(ad.getProvince());
        selectedCategory = ad.getCatId();
        categorySpinner.setText(ad.getCategory());
        selectedSubcategory = ad.getSctId();
        subcategorySpinner.setText(ad.getSubcategory());
        prepareSpinners();
    }

    private void notAuthorized() {
        //toast
        MRKUtil.toast(this, "sadadasdasda");
        SharedPrefUtil.getInstance(getApplicationContext()).removeUserData();
        startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_CODE);
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }

    private void modifyAd(final AdData adData) {
        callModifyAd = adService.modifyAd(token, adId, adData);
        callModifyAd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                addInProgress = false;
                if (response.isSuccessful()) {
                    modifySuccess(adData.isActive() != ad.isActive());
                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    addNotExists();
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    notAuthorized();
                } else {
                    showProgress(false);
                    connectionProblem();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!call.isCanceled()) {
                    addInProgress = false;
                    showProgress(false);
                    connectionProblem();
                }
            }
        });
    }

    private void modifySuccess(boolean toRemove) {
        MRKUtil.toast(this, getString(R.string.toast_ad_created));
        if(toRemove) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(POSITION_KEY, position);
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }

    private void connectionProblem() {
        MRKUtil.connectionProblem(this);
    }

    private void attemptAdd(boolean active) {
        if (addInProgress) {
            return;
        }

        addInProgress = true;
        title.setError(null);
        price.setError(null);
        categorySpinner.setError(null);
        subcategorySpinner.setError(null);
        city.setError(null);
        provinceSpinner.setError(null);
        phone.setError(null);

        String titleText = title.getText().toString().trim();
        String priceText = price.getText().toString();
        String descriptionText = description.getText().toString();
        String cityText = city.getText().toString().trim();
        String phoneText = phone.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(phoneText)) {
            phone.setError(getString(R.string.error_field_required));
            focusView = phone;
            cancel = true;
        } else if (!MRKUtil.isPhoneValid(this, phoneText, phone, false)) {
            phone.setError(getString(R.string.error_incorrect_phone));
            focusView = phone;
            cancel = true;
        }

        if (selectedProvince == null) {
            provinceSpinner.setError(getString(R.string.error_field_required));
            focusView = provinceSpinner;
            cancel = true;
        }

        if (TextUtils.isEmpty(cityText)) {
            city.setError(getString(R.string.error_field_required));
            focusView = city;
            cancel = true;
        }

        if (selectedCategory == null) {
            categorySpinner.setError(getString(R.string.error_field_required));
            focusView = categorySpinner;
            cancel = true;
        } else if (selectedSubcategory == null) {
            subcategorySpinner.setError(getString(R.string.error_field_required));
            focusView = subcategorySpinner;
            cancel = true;
        }

        if (TextUtils.isEmpty(priceText)) {
            price.setError(getString(R.string.error_field_required));
            focusView = price;
            cancel = true;
        }

        if (TextUtils.isEmpty(titleText)) {
            title.setError(getString(R.string.error_field_required));
            focusView = title;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            addInProgress = false;
        } else {
            showProgress(true);
            modifyAd(new AdData(titleText, priceText, selectedSubcategory, selectedProvince, descriptionText, cityText, phoneText, active));
        }
    }
}
