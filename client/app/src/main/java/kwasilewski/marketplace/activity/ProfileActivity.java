package kwasilewski.marketplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.helper.HintSpinner;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.listener.HintListener;
import kwasilewski.marketplace.retrofit.listener.UserListener;
import kwasilewski.marketplace.retrofit.manager.HintManager;
import kwasilewski.marketplace.retrofit.manager.UserManager;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import kwasilewski.marketplace.util.SpinnerUtil;
import kwasilewski.marketplace.util.ValidUtil;

public class ProfileActivity extends AppCompatActivity implements HintListener, UserListener, ErrorListener {

    private boolean inProgress;
    private boolean clicked;
    private HintManager hintManager;
    private UserManager userManager;
    private View progressBar;
    private View updateForm;
    private TextInputEditText firstNameField;
    private TextInputEditText lastNameField;
    private TextInputEditText cityField;
    private TextInputEditText phoneField;
    private HintSpinner provinceField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        hintManager = new HintManager(this, this);

        userManager = new UserManager(this, this);

        progressBar = findViewById(R.id.profile_progress);
        updateForm = findViewById(R.id.profile_form);

        firstNameField = findViewById(R.id.profile_first_name);
        lastNameField = findViewById(R.id.profile_last_name);
        cityField = findViewById(R.id.profile_city);
        phoneField = findViewById(R.id.profile_phone);
        phoneField.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (MRKUtil.checkIme(id)) {
                attemptModify();
                return true;
            }
            return false;
        });

        provinceField = findViewById(R.id.profile_province);
        provinceField.setOnItemClickListener((adapterView, view, position, l) -> SpinnerUtil.getClickedItemId(adapterView, position, provinceField));

        Button modifyButton = findViewById(R.id.profile_modify_button);
        modifyButton.setOnClickListener(view -> attemptModify());

        Button passwordButton = findViewById(R.id.profile_password_button);
        passwordButton.setOnClickListener(v -> goToPasswordChange());
    }

    @Override
    protected void onResume() {
        super.onResume();
        clicked = false;
        showProgress(true);
        hintManager.getProvinces(new ErrorListener() {
        });
        setUserData();
    }

    @Override
    protected void onPause() {
        hintManager.cancelCalls();
        userManager.cancelCalls();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MRKUtil.backButtonClicked(this, item);
        return super.onOptionsItemSelected(item);
    }

    private void setUserData() {
        UserData user = SharedPrefUtil.getInstance(this).getUserData();
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        cityField.setText(user.getCity());
        phoneField.setText(user.getPhone());
        provinceField.setText(user.getProvince());
        provinceField.setItemId(user.getPrvId());
    }

    private void showProgress(final boolean show) {
        inProgress = show;
        MRKUtil.showProgressBar(this, updateForm, progressBar, show);
    }

    private void goToPasswordChange() {
        if (!clicked) {
            clicked = true;
            startActivity(new Intent(this, PasswordActivity.class));
        }
    }

    private void attemptModify() {
        if (inProgress) {
            return;
        }

        inProgress = true;

        firstNameField.setError(null);
        cityField.setError(null);
        provinceField.setError(null);
        phoneField.setError(null);

        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String city = cityField.getText().toString();
        String phone = phoneField.getText().toString();
        Long province = provinceField.getItemId();

        boolean cancel = false;
        View focusView = null;

        if (!ValidUtil.phoneValid(this, phone, phoneField, true)) {
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

        if (ValidUtil.fieldEmpty(this, firstName, firstNameField)) {
            focusView = firstNameField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            inProgress = false;
        } else {
            showProgress(true);
            userManager.updateProfile(new UserData(firstName, lastName, city, province, phone), this);
        }
    }

    @Override
    public void provincesReceived(List<HintData> provinces) {
        SpinnerUtil.setHintAdapter(this, provinceField, provinces);
        showProgress(false);
    }

    @Override
    public void profileUpdated(UserData user) {
        SharedPrefUtil.getInstance(this).saveUserData(user);
        showProgress(false);
        MRKUtil.toast(this, getString(R.string.toast_profile_edited));
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        showProgress(false);
        MRKUtil.connectionProblem(this);
    }

}
