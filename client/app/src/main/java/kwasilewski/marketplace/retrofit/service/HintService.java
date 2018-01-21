package kwasilewski.marketplace.retrofit.service;

import java.util.List;

import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.dto.hint.HintData;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HintService {

    @GET(AppConstants.PROVINCES_PATH)
    Call<List<HintData>> getProvinces();

    @GET(AppConstants.HINTS_PATH)
    Call<ComboHintData> getAllHints();

}
