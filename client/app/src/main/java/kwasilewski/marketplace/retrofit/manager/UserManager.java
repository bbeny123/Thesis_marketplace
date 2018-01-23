package kwasilewski.marketplace.retrofit.manager;

import android.content.Context;

import kwasilewski.marketplace.dto.user.LoginData;
import kwasilewski.marketplace.dto.user.PasswordData;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.retrofit.RetrofitCallback;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.listener.UserListener;
import kwasilewski.marketplace.retrofit.service.UserService;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class UserManager {

    private final static UserService userService = RetrofitService.getInstance().getUserService();
    private Context context;
    private Call<ResponseBody> call;
    private Call<UserData> callUser;
    private UserListener userListener;
    private String token;

    public UserManager(Context context, UserListener userListener) {
        this.context = context;
        this.userListener = userListener;
        this.token = SharedPrefUtil.getInstance(context).getToken();
    }

    public void register(UserData user) {
        call = userService.register(user);
        call.enqueue(new RetrofitCallback<>(userListener::registered));
    }

    public void login(LoginData loginData) {
        callUser = userService.login(loginData);
        callUser.enqueue(new RetrofitCallback<>(userListener::logged));
    }

    public void validateToken() {
        callUser = userService.validateToken(token);
        callUser.enqueue(new RetrofitCallback<>(userListener::tokenValidated));
    }

    public void updateProfile(UserData user) {
        callUser = userService.updateProfile(token, user);
        callUser.enqueue(new RetrofitCallback<>(userListener::profileUpdated));
    }

    public void changePassword(PasswordData passwordData) {
        call = userService.changePassword(token, passwordData);
        call.enqueue(new RetrofitCallback<>(userListener::passwordChanged));
    }

    public void cancelCalls() {
        if (call != null) {
            call.cancel();
        }
        if (callUser != null) {
            callUser.cancel();
        }
    }

}
