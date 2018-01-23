package kwasilewski.marketplace.retrofit.manager;

import android.content.Context;

import java.util.List;

import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.retrofit.RetrofitCallback;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.listener.HintListener;
import kwasilewski.marketplace.retrofit.service.HintService;
import retrofit2.Call;

public class HintManager {

    private final static HintService hintService = RetrofitService.getInstance().getHintService();
    private Context context;
    private Call<ComboHintData> callCombo;
    private Call<List<HintData>> callProvince;
    private HintListener hintListener;

    public HintManager(Context context, HintListener hintListener) {
        this.context = context;
        this.hintListener = hintListener;
    }

    public void getAllHints() {
        callCombo = hintService.getAllHints();
        callCombo.enqueue(new RetrofitCallback<>(hintListener::hintsReceived));
    }

    public void getProvinces() {
        callProvince = hintService.getProvinces();
        callProvince.enqueue(new RetrofitCallback<>(hintListener::provincesReceived));
    }

    public void cancelCalls() {
        if (callCombo != null) {
            callCombo.cancel();
        }
        if (callProvince != null) {
            callProvince.cancel();
        }
    }

}
