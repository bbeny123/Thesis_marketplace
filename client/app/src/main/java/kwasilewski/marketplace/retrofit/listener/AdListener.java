package kwasilewski.marketplace.retrofit.listener;

import android.util.Log;

import java.util.List;

import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import okhttp3.ResponseBody;

public interface AdListener {

    default void adCreated(ResponseBody responseBody) {
        Log.d("RetrofitListener", "Unhandled adCreated");
    }

    default void adModified(ResponseBody responseBody) {
        Log.d("RetrofitListener", "Unhandled adModified");
    }

    default void adStatusChanged(ResponseBody responseBody) {
        Log.d("RetrofitListener", "Unhandled adStatusChanged");
    }

    default void adRefreshed(ResponseBody responseBody) {
        Log.d("RetrofitListener", "Unhandled adRefreshed");
    }

    default void favouriteAdded(ResponseBody responseBody) {
        Log.d("RetrofitListener", "Unhandled favouriteAdded");
    }

    default void favouriteRemoved(ResponseBody responseBody) {
        Log.d("RetrofitListener", "Unhandled favouriteRemoved");
    }

    default void adReceived(AdDetailsData ad) {
        Log.d("RetrofitListener", "Unhandled adReceived");
    }

    default void adsReceived(List<AdMinimalData> ads) {
        Log.d("RetrofitListener", "Unhandled adsReceived");
    }

}
