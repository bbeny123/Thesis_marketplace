package kwasilewski.marketplace.retrofit;

import android.app.Activity;

import java.net.HttpURLConnection;
import java.util.function.Consumer;

import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitCallback<T> implements Callback<T> {

    private final Consumer<T> function;
    private final Activity activity;
    private final ErrorListener errorListener;

    public RetrofitCallback(Consumer<T> function, Activity activity, ErrorListener errorListener) {
        this.function = function;
        this.activity = activity;
        this.errorListener = errorListener;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            function.accept(response.body());
        } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            errorListener.unauthorized(activity);
        } else if (response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
            errorListener.notAcceptable(activity);
        } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
            errorListener.notFound(activity);
        } else {
            errorListener.serverError(activity);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!call.isCanceled()) {
            errorListener.failure(activity);
        }
    }

}
