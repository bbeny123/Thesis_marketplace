package kwasilewski.marketplace.retrofit;

import android.content.Context;

import java.net.HttpURLConnection;
import java.util.function.Consumer;

import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitCallback<T> implements Callback<T> {

    private Consumer<T> function;
    private Context context;
    private ErrorListener errorListener;

    public RetrofitCallback(Consumer<T> function, Context context, ErrorListener errorListener) {
        this.function = function;
        this.context = context;
        this.errorListener = errorListener;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            function.accept(response.body());
        } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            errorListener.unauthorized(context);
        } else if (response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
            errorListener.notAcceptable(context);
        } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
            errorListener.notFound(context);
        } else {
            errorListener.serverError(context);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!call.isCanceled()) {
            errorListener.failure(context);
        }
    }

}
