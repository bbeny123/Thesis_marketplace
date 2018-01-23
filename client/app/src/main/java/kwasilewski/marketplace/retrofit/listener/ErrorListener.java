package kwasilewski.marketplace.retrofit.listener;

import android.content.Context;
import android.util.Log;

public interface ErrorListener {

    default void unauthorized(Context context) {
        Log.d("RetrofitListener", "Unhandled hintsReceived");
    }

    default void notAcceptable(Context context) {
        Log.d("RetrofitListener", "Unhandled provincesReceived");
    }

    default void notFound(Context context) {
        Log.d("RetrofitListener", "Unhandled provincesReceived");
    }

    default void serverError(Context context) {
        Log.d("RetrofitListener", "Unhandled provincesReceived");
    }

    default void failure(Context context) {
        Log.d("RetrofitListener", "Unhandled provincesReceived");
    }

}
