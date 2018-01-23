package kwasilewski.marketplace.retrofit;

import java.lang.reflect.Method;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitCallback<T> implements Callback<T> {

    private Object object;
    private Method method;

    public RetrofitCallback(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            T body = null;
            if (!(response.body() instanceof ResponseBody)) {
                body = response.body();
            }
            try {
                method.invoke(object, body);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!call.isCanceled()) {

        }
    }

}
