package kwasilewski.marketplace.retrofit.listener;

import android.app.Activity;
import android.util.Log;

import kwasilewski.marketplace.util.MRKUtil;

public interface ErrorListener {

    default void unauthorized(Activity activity) {
        Log.d("RetrofitListener", "Unhandled unauthorized");
        unhandledError(activity);
    }

    default void notAcceptable(Activity activity) {
        Log.d("RetrofitListener", "Unhandled notAcceptable");
        unhandledError(activity);
    }

    default void notFound(Activity activity) {
        Log.d("RetrofitListener", "Unhandled notFound");
        unhandledError(activity);
    }

    default void serverError(Activity activity) {
        Log.d("RetrofitListener", "Unhandled serverError");
        unhandledError(activity);
    }

    default void failure(Activity activity) {
        Log.d("RetrofitListener", "Unhandled failure");
        unhandledError(activity);
    }

    default void unhandledError(Activity activity) {
        Log.d("RetrofitListener", "Unhandled error");
        MRKUtil.connectionProblem(activity);
    }

}
