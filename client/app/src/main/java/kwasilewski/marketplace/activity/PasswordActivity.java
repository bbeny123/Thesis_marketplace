package kwasilewski.marketplace.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.net.HttpURLConnection;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.user.PasswordData;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.UserService;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {

    private boolean modifyInProgress = false;
    private UserService userService;
    private Call<ResponseBody> callUser;

    private View progressBar;
    private View passwordFormView;
    private TextInputEditText oldPasswordEditText;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText confirmPasswordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Toolbar toolbar = findViewById(R.id.password_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        userService = RetrofitService.getInstance().getUserService();

        progressBar = findViewById(R.id.password_progress);
        passwordFormView = findViewById(R.id.password_form);

        oldPasswordEditText = findViewById(R.id.password_old_password);
        newPasswordEditText = findViewById(R.id.password_new_password);
        confirmPasswordEditText = findViewById(R.id.password_confirm_password);

        Button modifyButton = findViewById(R.id.password_modify_button);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptModify();
            }
        });
    }

    @Override
    protected void onPause() {
        if(callUser != null) callUser.cancel();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBarHideView(this, passwordFormView, progressBar, show);
    }

    private void connectionProblem() {
        MRKUtil.connectionProblem(this);
    }

    private void modifySuccess() {
        MRKUtil.toast(this, getString(R.string.toast_password_changed));
        finish();
    }

    private void attemptModify() {
        if (modifyInProgress) {
            return;
        }

        modifyInProgress = true;
        oldPasswordEditText.setError(null);
        newPasswordEditText.setError(null);
        confirmPasswordEditText.setError(null);

        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = newPasswordEditText;
            cancel = true;
        } else if (!MRKUtil.isPasswordValid(newPassword)) {
            newPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = newPasswordEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = confirmPasswordEditText;
            cancel = true;
        }

        if (!cancel && !TextUtils.equals(newPassword, confirmPassword)) {
            newPasswordEditText.setError(getString(R.string.error_passwords_match));
            confirmPasswordEditText.setError(getString(R.string.error_passwords_match));
            focusView = confirmPasswordEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(oldPassword)) {
            oldPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = oldPasswordEditText;
            cancel = true;
        } else if (!MRKUtil.isPasswordValid(oldPassword)) {
            oldPasswordEditText.setError(getString(R.string.error_password_match));
            focusView = oldPasswordEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            modifyInProgress = false;
        } else {
            showProgress(true);
            String email = SharedPrefUtil.getInstance(this).getUserData().getEmail();
            modify(new PasswordData(MRKUtil.encodePassword(email, oldPassword), MRKUtil.encodePassword(email, newPassword)));
        }
    }

    private void modify(PasswordData passwordData) {
        callUser = userService.changePassword(SharedPrefUtil.getInstance(this).getToken(), passwordData);
        callUser.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                modifyInProgress = false;
                if (response.isSuccessful()) {
                    modifySuccess();
                } else if (response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                    showProgress(false);
                    oldPasswordEditText.setError(getString(R.string.error_password_match));
                    oldPasswordEditText.requestFocus();
                } else {
                    showProgress(false);
                    connectionProblem();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!call.isCanceled()) {
                    modifyInProgress = false;
                    showProgress(false);
                    connectionProblem();
                }
            }
        });
    }

}
