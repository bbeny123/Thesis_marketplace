package kwasilewski.marketplace.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.user.PasswordData;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.listener.UserListener;
import kwasilewski.marketplace.retrofit.manager.UserManager;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import kwasilewski.marketplace.util.ValidUtil;
import okhttp3.ResponseBody;

public class PasswordActivity extends AppCompatActivity implements UserListener, ErrorListener {

    private boolean inProgress;
    private UserManager userManager;

    private View progressBar;
    private View passwordForm;
    private TextInputEditText oldField;
    private TextInputEditText newField;
    private TextInputEditText confirmField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Toolbar toolbar = findViewById(R.id.password_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        userManager = new UserManager(this, this);

        progressBar = findViewById(R.id.password_progress);
        passwordForm = findViewById(R.id.password_form);

        oldField = findViewById(R.id.password_old_password);
        newField = findViewById(R.id.password_new_password);
        confirmField = findViewById(R.id.password_confirm_password);

        Button modifyButton = findViewById(R.id.password_modify_button);
        modifyButton.setOnClickListener(view -> attemptModify());
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void showProgress(final boolean show) {
        inProgress = show;
        MRKUtil.showProgressBar(this, passwordForm, progressBar, show);
    }

    private void attemptModify() {
        if (inProgress) {
            return;
        }

        inProgress = true;
        oldField.setError(null);
        newField.setError(null);
        confirmField.setError(null);

        String oldText = oldField.getText().toString();
        String newText = newField.getText().toString();
        String confirmText = confirmField.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!ValidUtil.passwordValid(this, newText, newField)) {
            focusView = newField;
            cancel = true;
        }

        if (ValidUtil.fieldEmpty(this, confirmText, confirmField)) {
            focusView = confirmField;
            cancel = true;
        }

        if (!cancel && !TextUtils.equals(newText, confirmText)) {
            confirmField.setError(getString(R.string.error_passwords_match));
            focusView = confirmField;
            cancel = true;
        }

        if (!ValidUtil.passwordValid(this, oldText, oldField)) {
            focusView = oldField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            inProgress = false;
        } else {
            showProgress(true);
            String email = SharedPrefUtil.getInstance(this).getUserData().getEmail();
            userManager.changePassword(new PasswordData(email, oldText, newText), this);
        }
    }

    @Override
    public void passwordChanged(ResponseBody response) {
        MRKUtil.toast(this, getString(R.string.toast_password_changed));
        finish();
    }

    @Override
    public void notAcceptable(Activity activity) {
        showProgress(false);
        oldField.setError(getString(R.string.error_password_match));
        oldField.requestFocus();
    }

    @Override
    public void unhandledError(Activity activity, String error) {
        showProgress(false);
        MRKUtil.connectionProblem(this);
    }

}
