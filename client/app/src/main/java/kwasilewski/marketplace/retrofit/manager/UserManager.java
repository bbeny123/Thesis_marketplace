package kwasilewski.marketplace.retrofit.manager;

import android.app.Activity;

import java.util.function.Consumer;

import kwasilewski.marketplace.dto.user.LoginData;
import kwasilewski.marketplace.dto.user.PasswordData;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.retrofit.RetrofitCallback;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.listener.UserListener;
import kwasilewski.marketplace.retrofit.service.UserService;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class UserManager {

    private final static UserService userService = RetrofitService.getInstance().getUserService();
    private Activity activity;
    private Call<ResponseBody> call;
    private Call<UserData> callUser;
    private UserListener userListener;
    private String token;
    private ErrorListener errorListener;

    public UserManager(Activity activity, UserListener userListener, ErrorListener errorListener) {
        this.activity = activity;
        this.userListener = userListener;
        this.token = SharedPrefUtil.getInstance(activity).getToken();
        this.errorListener = errorListener;
    }

    public void register(UserData user) {
        call = userService.register(user);
        call.enqueue(getRetrofitCallback(userListener::registered));
    }

    public void login(LoginData loginData) {
        callUser = userService.login(loginData);
        callUser.enqueue(getRetrofitCallback(userListener::logged));
    }

    public void validateToken() {
        callUser = userService.validateToken(token);
        callUser.enqueue(getRetrofitCallback(userListener::tokenValidated));
    }

    public void updateProfile(UserData user) {
        callUser = userService.updateProfile(token, user);
        callUser.enqueue(getRetrofitCallback(userListener::profileUpdated));
    }

    public void changePassword(PasswordData passwordData) {
        call = userService.changePassword(token, passwordData);
        call.enqueue(getRetrofitCallback(userListener::passwordChanged));
    }

    public void cancelCalls() {
        if (call != null) {
            call.cancel();
        }
        if (callUser != null) {
            callUser.cancel();
        }
    }

    private <T> RetrofitCallback<T> getRetrofitCallback(Consumer<T> function) {
        return new RetrofitCallback<>(function, activity, errorListener);
    }

}
