package kwasilewski.marketplace.retrofit.service;

import kwasilewski.marketplace.configuration.AppConstants;
import kwasilewski.marketplace.dto.user.LoginData;
import kwasilewski.marketplace.dto.user.PasswordData;
import kwasilewski.marketplace.dto.user.UserData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface UserService {

    @POST(AppConstants.REGISTER_PATH)
    Call<ResponseBody> register(@Body UserData user);

    @POST(AppConstants.LOGIN_PATH)
    Call<UserData> login(@Body LoginData loginData);

    @GET(AppConstants.TOKEN_PATH)
    Call<UserData> checkToken(@Header("token") String token);

    @PATCH(AppConstants.USER_PATH)
    Call<UserData> updateProfile(@Header("token") String token, @Body UserData user);

    @PATCH(AppConstants.USER_PASSWORD_PATH)
    Call<ResponseBody> changePassword(@Header("token") String token, @Body PasswordData passwordData);

}
