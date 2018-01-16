package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.net.HttpURLConnection;

import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.service.UserService;
import kwasilewski.marketplace.util.SharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.userService = RetrofitService.getInstance().getUserService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String token = SharedPref.getInstance(this).getToken();
        if (token == null) {
            goToLoginActivity();
        } else {
            checkToken(token);
        }
    }

    private void checkToken(String token) {
        Call<UserData> call = userService.checkToken(token);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if(response.isSuccessful()) {
                    tokenAuthorized(response.body());
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    tokenUnauthorized();
                } else {
                    goToLoginActivity();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                connectionProblem();
            }
        });
    }

    private void tokenAuthorized(UserData user) {
        SharedPref.getInstance(this).saveUserData(user);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void tokenUnauthorized() {
        SharedPref.getInstance(this).saveToken(null);
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void connectionProblem() {
        startActivity(new Intent(this, NetErrorActivity.class));
    }

}
