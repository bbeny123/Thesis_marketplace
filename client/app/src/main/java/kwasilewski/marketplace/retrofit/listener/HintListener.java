package kwasilewski.marketplace.retrofit.listener;

import android.util.Log;

import java.util.List;

import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.dto.hint.HintData;

public interface HintListener {

    default void hintsReceived(ComboHintData hints) {
        Log.d("RetrofitListener", "Unhandled hintsReceived");
    }

    default void provincesReceived(List<HintData> hints) {
        Log.d("RetrofitListener", "Unhandled provincesReceived");
    }

}
