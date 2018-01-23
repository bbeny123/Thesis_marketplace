package kwasilewski.marketplace.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdData;
import kwasilewski.marketplace.dto.hint.CategoryData;
import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.helper.DialogItem;
import kwasilewski.marketplace.helper.HintSpinner;
import kwasilewski.marketplace.helper.PhotoView;
import kwasilewski.marketplace.helper.PhotoViewList;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.HintService;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAddActivity extends AppCompatActivity {

    private final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private final int PERMISSION_CODE = 1;
    private final int MATISSE_CODE = 2;
    private final PhotoViewList photos = new PhotoViewList();
    private HintService hintService;
    private Call<ResponseBody> callAd;
    private Call<ComboHintData> callHint;
    private Long selectedProvince;
    private Long selectedCategory;
    private Long selectedSubcategory;
    private boolean addInProgress = false;
    private boolean spinnersSettingInProgress = false;
    private View progressBar;
    private View newFormView;
    private TextInputEditText titleEditText;
    private TextInputEditText priceEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText cityEditText;
    private TextInputEditText phoneEditText;
    private HintSpinner categorySpinner;
    private HintSpinner subcategorySpinner;
    private HintSpinner provinceSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add);

        Toolbar toolbar = findViewById(R.id.new_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        hintService = RetrofitService.getInstance().getHintService();

        progressBar = findViewById(R.id.new_progress);
        newFormView = findViewById(R.id.new_form);
        titleEditText = findViewById(R.id.new_title);
        priceEditText = findViewById(R.id.new_price);
        priceEditText.addTextChangedListener(MRKUtil.getTextWatcherPositiveNumber());
        categorySpinner = findViewById(R.id.new_category);
        categorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCategory = spinnerOnClickListener(categorySpinner, adapterView.getItemAtPosition(position));
            }
        });
        subcategorySpinner = findViewById(R.id.new_subcategory);
        enableSubcategorySpinner(false);
        subcategorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedSubcategory = spinnerOnClickListener(subcategorySpinner, adapterView.getItemAtPosition(position));
            }
        });
        descriptionEditText = findViewById(R.id.new_description);
        cityEditText = findViewById(R.id.new_city);
        provinceSpinner = findViewById(R.id.new_province);
        provinceSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedProvince = spinnerOnClickListener(provinceSpinner, adapterView.getItemAtPosition(position));
            }
        });

        phoneEditText = findViewById(R.id.new_phone);
        phoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (MRKUtil.checkIme(id)) attemptAdd();
                return MRKUtil.checkIme(id);
            }
        });

        Button addButton = findViewById(R.id.new_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAdd();
            }
        });

        photos.add((PhotoView) findViewById(R.id.new_image1));
        photos.add((PhotoView) findViewById(R.id.new_image2));
        photos.add((PhotoView) findViewById(R.id.new_image3));
        photos.add((PhotoView) findViewById(R.id.new_image4));
        photos.add((PhotoView) findViewById(R.id.new_image5));
        photos.add((PhotoView) findViewById(R.id.new_image6));
        photos.add((PhotoView) findViewById(R.id.new_image7));
        photos.add((PhotoView) findViewById(R.id.new_image8));
        photos.add((PhotoView) findViewById(R.id.new_image9));
        photos.add((PhotoView) findViewById(R.id.new_image10));

        for (PhotoView photo : photos.getPhotos()) {
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickPhoto(((PhotoView) v).getPosition());
                }
            });
        }

        setUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpSpinners();
    }

    @Override
    protected void onPause() {
        if (callAd != null) callAd.cancel();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults.length > 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    MRKUtil.toast(this, getString(R.string.toast_permissions));
                    return;
                }
            }
            callMatisse();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MATISSE_CODE && resultCode == RESULT_OK) {
            for (Uri uri : Matisse.obtainResult(data)) {
                photos.addPhoto(this, uri);
            }
        }
    }

    private boolean hasPermissions() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void callMatisse() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "kwasilewski.marketplace.fileprovider"))
                .countable(true)
                .maxSelectable(AppConstants.MAX_PHOTOS - photos.getPhotoContained())
                .imageEngine(new PicassoEngine())
                .theme(R.style.Matisse_Dracula)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .forResult(MATISSE_CODE);
    }

    private void clickPhoto(int position) {
        if (photos.containsPhoto(position)) {
            photoDialog(position);
        } else {
            if (!hasPermissions()) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
            } else {
                callMatisse();
            }
        }
    }

    private void photoDialog(final int position) {
        List<DialogItem> items = new ArrayList<>();
        if(position != 0) {
            items.add(new DialogItem(getString(R.string.action_set_thumbnail), android.R.drawable.ic_menu_gallery));
            items.add(new DialogItem(getString(R.string.action_remove_photo), android.R.drawable.ic_menu_delete));
        } else {
            items.add(new DialogItem(getString(R.string.action_remove_photo), android.R.drawable.ic_menu_delete));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(MRKUtil.getDialogAdapter(this, items), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    removePhoto(position);
                } else if (which == 0 && position == 0) {
                    removePhoto(position);
                } else if (which == 0) {
                    setMiniature(position);
                }
            }
        });
        builder.setPositiveButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void setMiniature(int position) {
        photos.setThumbnail(this, position);
    }

    private void removePhoto(int position) {
        photos.remove(this, position);
    }

    private void setUserData() {
        UserData user = SharedPrefUtil.getInstance(this).getUserData();
        cityEditText.setText(user.getCity());
        provinceSpinner.setText(user.getProvince());
        selectedProvince = user.getPrvId();
        phoneEditText.setText(user.getPhone());
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

    private void setProvinceAdapter(List<HintData> hintData) {
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, hintData);
        provinceSpinner.setAdapter(adapter);
    }

    private void setCategoryAdapter(List<CategoryData> categoryData) {
        ArrayAdapter<CategoryData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, categoryData);
        categorySpinner.setAdapter(adapter);
    }

    private void setSubcategoryAdapter(List<HintData> hintData) {
        selectedSubcategory = null;
        subcategorySpinner.setText(null);
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, hintData);
        subcategorySpinner.setAdapter(adapter);
        enableSubcategorySpinner(true);
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

    private void attemptAdd() {
        if (addInProgress) {
            return;
        }

        addInProgress = true;
        titleEditText.setError(null);
        priceEditText.setError(null);
        categorySpinner.setError(null);
        subcategorySpinner.setError(null);
        cityEditText.setError(null);
        provinceSpinner.setError(null);
        phoneEditText.setError(null);

        String title = titleEditText.getText().toString().trim();
        String price = priceEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String city = cityEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError(getString(R.string.error_field_required));
            focusView = phoneEditText;
            cancel = true;
        } else if (!MRKUtil.isPhoneValid(phone)) {
            phoneEditText.setError(getString(R.string.error_incorrect_phone));
            focusView = phoneEditText;
            cancel = true;
        }

        if (selectedProvince == null) {
            provinceSpinner.setError(getString(R.string.error_field_required));
            focusView = provinceSpinner;
            cancel = true;
        }

        if (TextUtils.isEmpty(city)) {
            cityEditText.setError(getString(R.string.error_field_required));
            focusView = cityEditText;
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

        if (TextUtils.isEmpty(price)) {
            priceEditText.setError(getString(R.string.error_field_required));
            focusView = priceEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(title)) {
            titleEditText.setError(getString(R.string.error_field_required));
            focusView = titleEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            addInProgress = false;
        } else {
            showProgress(true);
            add(new AdData(title, price, selectedSubcategory, selectedProvince, description, city, phone, photos.getEncodedThumbnail(this), photos.getEncodedPhotos(this)));
        }
    }

    private void add(AdData adData) {
        callAd = RetrofitService.getInstance().getAdService().createAd(SharedPrefUtil.getInstance(this).getToken(), adData);
        callAd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                addInProgress = false;
                if (response.isSuccessful()) {
                    addSuccess();
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

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBarHideView(this, newFormView, progressBar, show);
    }

    private void connectionProblem() {
        MRKUtil.connectionProblem(this);
    }

    private void connectionProblemAtStart() {
        startActivity(new Intent(this, NetErrorActivity.class));
    }

    private void addSuccess() {
        MRKUtil.toast(this, getString(R.string.toast_ad_created));
        finish();
    }

}
