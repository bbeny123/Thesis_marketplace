package kwasilewski.marketplace.retrofit;

import kwasilewski.marketplace.retrofit.service.AdService;
import kwasilewski.marketplace.retrofit.service.HintService;
import kwasilewski.marketplace.retrofit.service.UserService;
import kwasilewski.marketplace.util.AppConstants;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static RetrofitService instance;
    private final AdService adService;
    private final HintService hintService;
    private final UserService userService;

    private RetrofitService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();
        adService = retrofit.create(AdService.class);
        hintService = retrofit.create(HintService.class);
        userService = retrofit.create(UserService.class);
    }

    public static RetrofitService getInstance() {
        if (instance == null) {
            synchronized (RetrofitService.class) {
                if (instance == null) {
                    instance = new RetrofitService();
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
