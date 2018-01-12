package kwasilewski.marketplace.service;

import kwasilewski.marketplace.dto.user.LoginData;
import kwasilewski.marketplace.dto.user.UserData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface UserService {

    @POST("/register")
    Call<ResponseBody> register(@Body UserData user);

    @POST("/login")
    Call<UserData> login(@Body LoginData loginData);

    @GET("/token")
    Call<UserData> checkToken(@Header("token") String token);

    @PATCH("/user")
    Call<ResponseBody> updateProfile(@Header("token") String token, @Body UserData user);

}
