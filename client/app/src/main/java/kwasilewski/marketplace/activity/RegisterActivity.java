package kwasilewski.marketplace.activity;

import android.app.Activity;
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
import kwasilewski.marketplace.util.SpinnerUtil;
import kwasilewski.marketplace.util.ValidUtil;
import okhttp3.ResponseBody;

public class RegisterActivity extends AppCompatActivity implements UserListener, HintListener, ErrorListener {

    private boolean inProgress;
    private HintManager hintManager;
    private UserManager userManager;

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

        hintManager = new HintManager(this, this);
        userManager = new UserManager(this, this);

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
        provinceField.setOnItemClickListener((adapter, view, position, l) -> SpinnerUtil.getClickedItemId(adapter, position, provinceField));

        Button singUpButton = findViewById(R.id.register_button);
        singUpButton.setOnClickListener(view -> attemptRegister());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress(true);
        hintManager.getProvinces(new ErrorListener() {
        });
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
        if (inProgress) {
            return;
        }

        inProgress = true;

        emailField.setError(null);
        passwordField.setError(null);
        firstNameField.setError(null);
        cityField.setError(null);
        provinceField.setError(null);
        phoneField.setError(null);

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
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

        if (!ValidUtil.passwordValid(this, password, passwordField)) {
            focusView = passwordField;
            cancel = true;
        }

        if (!ValidUtil.emailValid(this, email, emailField)) {
            focusView = emailField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            inProgress = false;
        } else {
            showProgress(true);
            userManager.register(new UserData(email, password, firstName, lastName, city, province, phone), this);
        }
    }

    private void showProgress(final boolean show) {
        inProgress = show;
        MRKUtil.showProgressBar(this, registerForm, progressBar, show);
    }

    @Override
    public void provincesReceived(List<HintData> provinces) {
        SpinnerUtil.setHintAdapter(this, provinceField, provinces);
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
