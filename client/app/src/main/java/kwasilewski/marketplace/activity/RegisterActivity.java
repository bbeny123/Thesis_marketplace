package kwasilewski.marketplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
import okhttp3.ResponseBody;

public class RegisterActivity extends AppCompatActivity implements UserListener, HintListener, ErrorListener {

    private boolean registerOn = false;
    private HintManager hintManager;
    private UserManager userManager;
    private Long province;

    private View progressBar;
    private View registerForm;
    private TextInputEditText emailField;
    private TextInputEditText passwordField;
    private TextInputEditText firstNameField;
    private TextInputEditText lastNameField;
    private TextInputEditText cityField;
    private TextInputEditText phoneField;
    private HintSpinner provinceField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.register_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        hintManager = new HintManager(this, this, new ErrorListener() {
            @Override
            public void unhandledError(Activity activity, String error) {
                startActivity(new Intent(getApplicationContext(), NetErrorActivity.class));
            }
        });

        userManager = new UserManager(this, this, this);

        progressBar = findViewById(R.id.register_progress);
        registerForm = findViewById(R.id.register_form);

        emailField = findViewById(R.id.register_email);
        passwordField = findViewById(R.id.register_password);
        firstNameField = findViewById(R.id.register_first_name);
        lastNameField = findViewById(R.id.register_last_name);
        cityField = findViewById(R.id.register_city);
        phoneField = findViewById(R.id.register_phone);
        phoneField.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (MRKUtil.checkIme(id)) {
                attemptRegister();
                return true;
            }
            return false;
        });

        provinceField = findViewById(R.id.register_province);
        provinceField.setOnItemClickListener((adapter, view, position, l) -> province = MRKUtil.getClickedItemId(adapter, position, provinceField, province));

        Button singUpButton = findViewById(R.id.register_button);
        singUpButton.setOnClickListener(view -> attemptRegister());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress(true);
        hintManager.getProvinces();
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

    private void attemptRegister() {
        if (registerOn) {
            return;
        }

        registerOn = true;

        emailField.setError(null);
        passwordField.setError(null);
        firstNameField.setError(null);
        cityField.setError(null);
        provinceField.setError(null);
        phoneField.setError(null);

        String emailText = emailField.getText().toString();
        String passwordText = passwordField.getText().toString();
        String firstNameText = firstNameField.getText().toString();
        String lastNameText = lastNameField.getText().toString();
        String cityText = cityField.getText().toString();
        String phoneText = phoneField.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!MRKUtil.isPhoneValid(this, phoneText, phoneField, true)) {
            focusView = phoneField;
            cancel = true;
        }

        if (MRKUtil.spinnerEmpty(this, province, provinceField)) {
            focusView = provinceField;
            cancel = true;
        }

        if (MRKUtil.fieldEmpty(this, cityText, cityField)) {
            focusView = cityField;
            cancel = true;
        }

        if (MRKUtil.fieldEmpty(this, firstNameText, firstNameField)) {
            focusView = firstNameField;
            cancel = true;
        }

        if (!MRKUtil.isPasswordValid(this, passwordText, passwordField)) {
            focusView = passwordField;
            cancel = true;
        }

        if (!MRKUtil.isEmailValid(this, emailText, emailField)) {
            focusView = emailField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            registerOn = false;
        } else {
            showProgress(true);
            userManager.register(new UserData(emailText, passwordText, firstNameText, lastNameText, cityText, province, phoneText));
        }
    }

    private void showProgress(final boolean show) {
        registerOn = show;
        MRKUtil.showProgressBarHideView(this, registerForm, progressBar, show);
    }

    @Override
    public void provincesReceived(List<HintData> provinces) {
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, provinces);
        provinceField.setAdapter(adapter);
        showProgress(false);
    }

    @Override
    public void registered(ResponseBody response) {
        MRKUtil.toast(this, getString(R.string.toast_register_successful));
        finish();
    }

    @Override
    public void notAcceptable(Activity activity) {
        showProgress(false);
        emailField.setError(getString(R.string.error_email_taken));
        emailField.requestFocus();
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        showProgress(false);
        MRKUtil.connectionProblem(this);
    }

}
