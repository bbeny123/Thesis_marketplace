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
    private ErrorListener errorListener;

    public HintManager(Activity activity, HintListener hintListener, ErrorListener errorListener) {
        this.activity = activity;
        this.hintListener = hintListener;
        this.errorListener = errorListener;
    }

    public void getAllHints() {
        callCombo = hintService.getAllHints();
        callCombo.enqueue(getRetrofitCallback(hintListener::hintsReceived));
    }

    public void getProvinces() {
        callProvince = hintService.getProvinces();
        callProvince.enqueue(getRetrofitCallback(hintListener::provincesReceived));
    }

    public void cancelCalls() {
        if (callCombo != null) {
            callCombo.cancel();
        }
        if (callProvince != null) {
            callProvince.cancel();
        }
    }

    private <T> RetrofitCallback<T> getRetrofitCallback(Consumer<T> function) {
        return new RetrofitCallback<>(function, activity, errorListener);
    }

}
