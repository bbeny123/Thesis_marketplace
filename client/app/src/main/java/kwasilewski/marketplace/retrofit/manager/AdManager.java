package kwasilewski.marketplace.retrofit.manager;

import kwasilewski.marketplace.dto.ad.AdData;
import kwasilewski.marketplace.retrofit.RetrofitCallback;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.listener.AdListener;
import kwasilewski.marketplace.retrofit.service.AdService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AdManager {

    private final static AdService adService = RetrofitService.getInstance().getAdService();
    private static Call<ResponseBody> callAd;
    private static Callback<ResponseBody> callbackAd =

    public  void createAd(AdListener listener, AdData adData) throws NoSuchMethodException {
        callAd = adService.newAd(token, adData);
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = null;
        callAd.enqueue(new RetrofitCallback<>(listener, AdListener.class.getMethod("adCreated", parameterTypes)));
    }

}
