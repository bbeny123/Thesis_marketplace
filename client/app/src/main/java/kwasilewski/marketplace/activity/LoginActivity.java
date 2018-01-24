package kwasilewski.marketplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.user.LoginData;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.listener.UserListener;
import kwasilewski.marketplace.retrofit.manager.UserManager;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;

public class LoginActivity extends AppCompatActivity implements UserListener, ErrorListener {

    private UserManager userManager;
    private boolean loginInProgress = false;

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private View progressBar;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.login_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        userManager = new UserManager(this, this, this);

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        passwordEditText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (MRKUtil.checkIme(id)) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button signInButton = findViewById(R.id.login_button);
        signInButton.setOnClickListener(v -> attemptLogin());

        Button registerButton = findViewById(R.id.login_register_button);
        registerButton.setOnClickListener(view -> goToRegister());

        loginFormView = findViewById(R.id.login_form);
        progressBar = findViewById(R.id.login_progress);
    }

    @Override
    protected void onPause() {
        userManager.cancelCalls();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void attemptLogin() {
        if (loginInProgress) {
            return;
        }

        loginInProgress = true;
        emailEditText.setError(null);
        passwordEditText.setError(null);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            focusView = passwordEditText;
            cancel = true;
        } else if (!MRKUtil.isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            cancel = true;
        } else if (!MRKUtil.isEmailValid(email)) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            focusView = emailEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            loginInProgress = false;
        } else {
            showProgress(true);
            login(new LoginData(email, MRKUtil.encodePassword(email, password)));
        }
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBarHideView(this, loginFormView, progressBar, show);
    }

    private void login(LoginData loginData) {
        userManager.login(loginData);
    }

    @Override
    public void logged(UserData user) {
        loginSuccessful(user);
        loginInProgress = false;
    }

    @Override
    public void unauthorized(Activity activity) {
        passwordEditText.setError(getString(R.string.error_invalid_password_email));
        passwordEditText.requestFocus();
        loginInProgress = false;
        showProgress(false);
    }

    @Override
    public void unhandledError(Activity activity) {
        loginInProgress = false;
        showProgress(false);
        MRKUtil.connectionProblem(this);
    }

    private void loginSuccessful(UserData user) {
        SharedPrefUtil.getInstance(this).saveLoginData(user);
        setResult(RESULT_OK);
        finish();
    }

}

