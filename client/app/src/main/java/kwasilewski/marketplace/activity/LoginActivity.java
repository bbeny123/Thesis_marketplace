package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpURLConnection;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.user.LoginData;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.UserService;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private boolean loginInProgress = false;
    private UserService userService;

    private EditText emailEditText;
    private EditText passwordEditText;
    private View progressBar;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        userService = RetrofitService.getInstance().getUserService();

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = findViewById(R.id.login_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button registerButton = findViewById(R.id.login_register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressBar = findViewById(R.id.login_progress);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            MRKUtil.toast(this, getResources().getString(R.string.toast_register_successful));
        }
    }

    private void goToRegister() {
        startActivityForResult(new Intent(this, RegisterActivity.class), 1);
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
        } else if (!isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
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

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBarHideView(this, loginFormView, progressBar, show);
    }

    private void login(LoginData loginData) {
        Call<UserData> call = userService.login(loginData);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                loginInProgress = false;
                if (response.isSuccessful()) {
                    loginSuccessful(response.body());
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    showProgress(false);
                    passwordEditText.setError(getString(R.string.error_invalid_password_email));
                    passwordEditText.requestFocus();
                } else {
                    showProgress(false);
                    connectionProblem();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                loginInProgress = false;
                showProgress(false);
                connectionProblem();
            }
        });
    }

    private void loginSuccessful(UserData user) {
        SharedPref.getInstance(this).saveUserData(user);
        SharedPref.getInstance(this).saveToken(user.getToken());
        setResult(RESULT_OK);
        finish();
    }

    private void connectionProblem() {
        MRKUtil.connectionProblem(this);
    }

}

