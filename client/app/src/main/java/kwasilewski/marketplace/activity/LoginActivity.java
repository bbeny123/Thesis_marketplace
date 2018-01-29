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

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.user.LoginData;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.listener.UserListener;
import kwasilewski.marketplace.retrofit.manager.UserManager;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import kwasilewski.marketplace.util.ValidUtil;

public class LoginActivity extends AppCompatActivity implements UserListener, ErrorListener {

    private boolean inProgress;
    private boolean clicked;
    private UserManager userManager;

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

        userManager = new UserManager(this, this);

        progressBar = findViewById(R.id.login_progress);
        loginFormView = findViewById(R.id.login_form);

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

        Button registerButton = findViewById(R.id.login_button_register);
        registerButton.setOnClickListener(view -> goToRegister());
    }

    @Override
    protected void onResume() {
        super.onResume();
        clicked = false;
        inProgress = false;
    }

    @Override
    protected void onPause() {
        userManager.cancelCalls();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MRKUtil.backButtonClicked(this, item);
        return super.onOptionsItemSelected(item);
    }

    private void attemptLogin() {
        if (inProgress) {
            return;
        }

        inProgress = true;

        emailEditText.setError(null);
        passwordEditText.setError(null);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!ValidUtil.passwordValid(this, password, passwordEditText)) {
            focusView = passwordEditText;
            cancel = true;
        }

        if (!ValidUtil.emailValid(this, email, emailEditText)) {
            focusView = emailEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            inProgress = false;
        } else {
            showProgress(true);
            userManager.login(new LoginData(email, password), this);
        }
    }

    private void goToRegister() {
        if (!clicked) {
            clicked = true;
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBar(this, loginFormView, progressBar, show);
    }

    @Override
    public void logged(UserData user) {
        SharedPrefUtil.getInstance(this).saveLoginData(user);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void unauthorized(Activity activity) {
        inProgress = false;
        showProgress(false);
        passwordEditText.setError(getString(R.string.error_credentials_invalid));
        passwordEditText.requestFocus();
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        inProgress = false;
        showProgress(false);
        MRKUtil.connectionProblem(this);
    }

}

