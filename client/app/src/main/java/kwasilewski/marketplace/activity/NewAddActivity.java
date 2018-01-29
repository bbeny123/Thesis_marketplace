package kwasilewski.marketplace.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdData;
import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.helper.DialogItem;
import kwasilewski.marketplace.helper.HintSpinner;
import kwasilewski.marketplace.helper.PhotoView;
import kwasilewski.marketplace.helper.PhotoViewList;
import kwasilewski.marketplace.retrofit.listener.AdListener;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.listener.HintListener;
import kwasilewski.marketplace.retrofit.manager.AdManager;
import kwasilewski.marketplace.retrofit.manager.HintManager;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import kwasilewski.marketplace.util.SpinnerUtil;
import kwasilewski.marketplace.util.ValidUtil;
import okhttp3.ResponseBody;

public class NewAddActivity extends AppCompatActivity implements HintListener, AdListener, ErrorListener {

    private final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private final int PERMISSION_CODE = 1;
    private final int GALLERY_CODE = 2;

    private final PhotoViewList photos = new PhotoViewList();

    private boolean inProgress;
    private HintManager hintManager;
    private AdManager adManager;

    private View progressBar;
    private View newForm;
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
        setContentView(R.layout.activity_new_add);

        Toolbar toolbar = findViewById(R.id.new_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        adManager = new AdManager(this, this);
        hintManager = new HintManager(this, this);

        progressBar = findViewById(R.id.new_progress);
        newForm = findViewById(R.id.new_form);

        titleField = findViewById(R.id.new_title);
        priceField = findViewById(R.id.new_price);
        priceField.addTextChangedListener(ValidUtil.positiveNumber());
        descriptionField = findViewById(R.id.new_description);
        cityField = findViewById(R.id.new_city);
        phoneField = findViewById(R.id.new_phone);
        phoneField.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (MRKUtil.checkIme(id)) {
                attemptAdd();
                return true;
            }
            return false;
        });

        provinceField = findViewById(R.id.new_province);
        provinceField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, provinceField));
        categoryField = findViewById(R.id.new_category);
        categoryField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, categoryField, this, subcategoryField, false));
        subcategoryField = findViewById(R.id.new_subcategory);
        subcategoryField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, subcategoryField));
        SpinnerUtil.disableSpinner(subcategoryField);

        Button addButton = findViewById(R.id.new_add_button);
        addButton.setOnClickListener(
                view -> attemptAdd());

        photos.add(findViewById(R.id.new_image1));
        photos.add(findViewById(R.id.new_image2));
        photos.add(findViewById(R.id.new_image3));
        photos.add(findViewById(R.id.new_image4));
        photos.add(findViewById(R.id.new_image5));
        photos.add(findViewById(R.id.new_image6));
        photos.add(findViewById(R.id.new_image7));
        photos.add(findViewById(R.id.new_image8));
        photos.add(findViewById(R.id.new_image9));
        photos.add(findViewById(R.id.new_image10));
        photos.getPhotos().forEach(photo -> photo.setOnClickListener(v -> clickPhoto(((PhotoView) v).getPosition())));

        setUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress(true);
        hintManager.getAllHints(new ErrorListener() {
        });
    }

    @Override
    protected void onPause() {
        hintManager.cancelCalls();
        adManager.cancelCalls();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MRKUtil.backButtonClicked(this, item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults.length > 0) {
            if (Arrays.stream(grantResults).anyMatch(result -> result != PackageManager.PERMISSION_GRANTED)) {
                MRKUtil.toast(this, getString(R.string.toast_permissions));
                return;
            }
            callMatisse();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Matisse.obtainResult(data).forEach(uri -> photos.addPhoto(this, uri));
        }
    }

    private void clickPhoto(int position) {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        } else if (photos.containsPhoto(position)) {
            photoDialog(position);
        } else {
            callMatisse();
        }
    }

    private boolean hasPermissions() {
        return Arrays.stream(PERMISSIONS).noneMatch(permission -> ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED);
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
                .forResult(GALLERY_CODE);
    }

    private void photoDialog(final int position) {
        List<DialogItem> items = new ArrayList<>();
        if (position != 0) {
            items.add(new DialogItem(getString(R.string.button_thumbnail), android.R.drawable.ic_menu_gallery));
        }
        items.add(new DialogItem(getString(R.string.button_remove), android.R.drawable.ic_menu_delete));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(DialogItem.getDialogAdapter(this, items), (dialog, which) -> {
            if (which == 0 && position != 0) {
                photos.setThumbnail(this, position);
            } else {
                photos.remove(this, position);
            }
        });
        builder.setPositiveButton(getString(R.string.button_cancel), null);
        builder.show();
    }

    private void setUserData() {
        UserData user = SharedPrefUtil.getInstance(this).getUserData();
        cityField.setText(user.getCity());
        provinceField.setText(user.getProvince());
        provinceField.setItemId(user.getPrvId());
        phoneField.setText(user.getPhone());
    }

    @Override
    public void hintsReceived(ComboHintData hints) {
        SpinnerUtil.setHintAdapter(this, provinceField, hints.getProvinces());
        SpinnerUtil.setHintAdapter(this, categoryField, hints.getCategories());
        showProgress(false);
    }

    private void attemptAdd() {
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
            adManager.createAd(new AdData(title, price, subcategory, province, description, city, phone, photos.getEncodedThumbnail(this), photos.getEncodedPhotos(this)), this);
        }
    }

    private void showProgress(final boolean show) {
        inProgress = show;
        MRKUtil.showProgressBar(this, newForm, progressBar, show);
    }

    @Override
    public void adCreated(ResponseBody responseBody) {
        MRKUtil.toast(this, getString(R.string.toast_ad_created));
        finish();
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        showProgress(false);
        MRKUtil.connectionProblem(this);
    }

}
