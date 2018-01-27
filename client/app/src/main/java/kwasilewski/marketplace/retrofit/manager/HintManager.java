package kwasilewski.marketplace.retrofit.manager;

import android.app.Activity;

import java.util.List;
import java.util.function.Consumer;

import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.retrofit.RetrofitCallback;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.listener.HintListener;
import kwasilewski.marketplace.retrofit.service.HintService;
import retrofit2.Call;

public class HintManager {

    private final static HintService hintService = RetrofitService.getInstance().getHintService();
    private Activity activity;
    private Call<ComboHintData> callCombo;
    private Call<List<HintData>> callProvince;
    private HintListener hintListener;

    public HintManager(Activity activity, HintListener hintListener) {
        this.activity = activity;
        this.hintListener = hintListener;
    }

    public void getAllHints(ErrorListener errorListener) {
        callCombo = hintService.getAllHints();
        callCombo.enqueue(getRetrofitCallback(hintListener::hintsReceived, errorListener));
    }

    public void getProvinces(ErrorListener errorListener) {
        callProvince = hintService.getProvinces();
        callProvince.enqueue(getRetrofitCallback(hintListener::provincesReceived, errorListener));
    }

    public void cancelCalls() {
        if (callCombo != null) {
            callCombo.cancel();
        }
        if (callProvince != null) {
            callProvince.cancel();
        }
    }

    private <T> RetrofitCallback<T> getRetrofitCallback(Consumer<T> function, ErrorListener errorListener) {
        return new RetrofitCallback<>(function, activity, errorListener);
    }

}
