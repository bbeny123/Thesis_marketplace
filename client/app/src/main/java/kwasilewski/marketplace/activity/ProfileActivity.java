package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.helper.HintSpinner;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.HintService;
import kwasilewski.marketplace.retrofit.service.UserService;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private boolean modifyInProgress = false;
    private UserService userService;
    private HintService hintService;
    private Long selectedProvince;
    private Call<UserData> callUser;
    private Call<List<HintData>> callHint;

    private View progressBar;
    private View modifyFormView;
    private TextInputEditText firstNameEditText;
    private TextInputEditText lastNameEditText;
    private TextInputEditText cityEditText;
    private HintSpinner provinceSpinner;
    private TextInputEditText phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        MRKUtil.setToolbar(this, toolbar);

        userService = RetrofitService.getInstance().getUserService();
        hintService = RetrofitService.getInstance().getHintService();

        progressBar = findViewById(R.id.profile_progress);
        modifyFormView = findViewById(R.id.profile_form);
        firstNameEditText = findViewById(R.id.profile_first_name);
        lastNameEditText = findViewById(R.id.profile_last_name);
        cityEditText = findViewById(R.id.profile_city);
        phoneEditText = findViewById(R.id.profile_phone);
        phoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptModify();
                    return true;
                }
                return false;
            }
        });

        provinceSpinner = findViewById(R.id.profile_province);
        provinceSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object item = adapterView.getItemAtPosition(position);
                if (item instanceof HintData) {
                    selectedProvince = ((HintData) item).getId();
                }
                provinceSpinner.setError(null);
            }
        });

        Button modifyButton = findViewById(R.id.profile_modify_button);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptModify();
            }
        });

        Button passwordButton = findViewById(R.id.profile_password_button);
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPasswordChange();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress(true);
        populateProvinceSpinner();
        setUserData();
    }

    @Override
    protected void onPause() {
        if(callUser != null) callUser.cancel();
        if(callHint != null) callHint.cancel();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUserData() {
        UserData user = SharedPrefUtil.getInstance(this).getUserData();
        firstNameEditText.setText(user.getFirstName());
        lastNameEditText.setText(user.getLastName());
        cityEditText.setText(user.getCity());
        phoneEditText.setText(user.getPhone());
        provinceSpinner.setText(user.getProvince());
        selectedProvince = user.getPrvId();
    }

    private void setSpinnerAdapter(List<HintData> hintData) {
        ArrayAdapter<HintData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, hintData);
        provinceSpinner.setAdapter(adapter);
        showProgress(false);
    }

    private void showProgress(final boolean show) {
        MRKUtil.showProgressBarHideView(this, modifyFormView, progressBar, show);
    }

    private void connectionProblem() {
        MRKUtil.connectionProblem(this);
    }

    private void connectionProblemAtStart() {
        startActivity(new Intent(this, NetErrorActivity.class));
    }

    private void goToPasswordChange() {
        startActivity(new Intent(this, PasswordActivity.class));
    }

    private void modifySuccess(UserData user) {
        SharedPrefUtil.getInstance(this).saveUserData(user);
        showProgress(false);
        MRKUtil.toast(this, getString(R.string.toast_profile_edited));
    }

    private void populateProvinceSpinner() {
        callHint = hintService.getProvinces();
        callHint.enqueue(new Callback<List<HintData>>() {
            @Override
            public void onResponse(Call<List<HintData>> call, Response<List<HintData>> response) {
                if (response.isSuccessful()) {
                    setSpinnerAdapter(response.body());
                } else {
                    connectionProblemAtStart();
                }
            }

            @Override
            public void onFailure(Call<List<HintData>> call, Throwable t) {
                if (!call.isCanceled()) connectionProblemAtStart();
            }
        });
    }

    private void attemptModify() {
        if (modifyInProgress) {
            return;
        }

        modifyInProgress = true;
        firstNameEditText.setError(null);
        lastNameEditText.setError(null);
        cityEditText.setError(null);
        provinceSpinner.setError(null);
        phoneEditText.setError(null);

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(phone) && !MRKUtil.isPhoneValid(phone)) {
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

        if (TextUtils.isEmpty(firstName)) {
            lastNameEditText.setError(getString(R.string.error_field_required));
            focusView = lastNameEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            modifyInProgress = false;
        } else {
            showProgress(true);
            modify(new UserData(firstName, lastName, city, selectedProvince, phone));
        }
    }

    private void modify(UserData userData) {
        callUser = userService.updateProfile(SharedPrefUtil.getInstance(this).getToken(), userData);
        callUser.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                modifyInProgress = false;
                if (response.isSuccessful()) {
                    modifySuccess(response.body());
                } else {
                    showProgress(false);
                    connectionProblem();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                if (!call.isCanceled()) {
                    modifyInProgress = false;
                    showProgress(false);
                    connectionProblem();
                }
            }
        });
    }

}
