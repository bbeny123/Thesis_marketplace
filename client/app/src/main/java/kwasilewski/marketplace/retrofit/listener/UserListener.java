package kwasilewski.marketplace.retrofit.listener;

import android.util.Log;

import kwasilewski.marketplace.dto.user.UserData;
import okhttp3.ResponseBody;

public interface UserListener {

    default void registered(ResponseBody responseBody) {
        Log.d("RetrofitListener", "Unhandled registered");
    }

    default void logged(UserData user) {
        Log.d("RetrofitListener", "Unhandled logged");
    }

    default void tokenValidated(UserData user) {
        Log.d("RetrofitListener", "Unhandled tokenValidated");
    }

    default void profileUpdated(UserData user) {
        Log.d("RetrofitListener", "Unhandled profileUpdated");
    }

    default void passwordChanged(ResponseBody responseBody) {
        Log.d("RetrofitListener", "Unhandled passwordChanged");
    }

}
