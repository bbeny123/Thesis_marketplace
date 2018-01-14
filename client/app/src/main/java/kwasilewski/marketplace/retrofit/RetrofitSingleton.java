package kwasilewski.marketplace.retrofit;

import kwasilewski.marketplace.retrofit.service.AdService;
import kwasilewski.marketplace.retrofit.service.HintService;
import kwasilewski.marketplace.retrofit.service.UserService;
import kwasilewski.marketplace.configuration.AppConstants;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    private static RetrofitSingleton instance;
    private AdService adService;
    private HintService hintService;
    private UserService userService;

    private RetrofitSingleton() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();
        adService = retrofit.create(AdService.class);
        hintService = retrofit.create(HintService.class);
        userService = retrofit.create(UserService.class);
    }

    public static RetrofitSingleton getInstance() {
        if (instance == null) {
            synchronized (RetrofitSingleton.class) {
                if (instance == null) {
                    instance = new RetrofitSingleton();
                }
            }
        }
        return instance;
    }

    public AdService getAdService() {
        return adService;
    }

    public HintService getHintService() {
        return hintService;
    }

    public UserService getUserService() {
        return userService;
    }

}
