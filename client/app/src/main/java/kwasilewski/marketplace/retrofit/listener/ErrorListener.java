package kwasilewski.marketplace.retrofit.listener;

import android.app.Activity;
import android.util.Log;

import kwasilewski.marketplace.util.MRKUtil;

public interface ErrorListener {

    default void unauthorized(Activity activity) {
        MRKUtil.connectionProblem(activity);
        Log.d("RetrofitListener", "Unhandled unauthorized");
    }

    default void notAcceptable(Activity activity) {
        MRKUtil.connectionProblem(activity);
        Log.d("RetrofitListener", "Unhandled notAcceptable");
    }

    default void notFound(Activity activity) {
        MRKUtil.connectionProblem(activity);
        Log.d("RetrofitListener", "Unhandled notFound");
    }

    default void serverError(Activity activity) {
        MRKUtil.connectionProblem(activity);
        Log.d("RetrofitListener", "Unhandled serverError");
    }

    default void failure(Activity activity) {
        MRKUtil.connectionProblem(activity);
        Log.d("RetrofitListener", "Unhandled failure");
    }

}
