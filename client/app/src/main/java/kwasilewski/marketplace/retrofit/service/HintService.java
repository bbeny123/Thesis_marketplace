package kwasilewski.marketplace.retrofit.service;

import java.util.List;

import kwasilewski.marketplace.dto.hint.ComboHintData;
import kwasilewski.marketplace.dto.hint.HintData;
import kwasilewski.marketplace.util.AppConstants;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HintService {

    @GET(AppConstants.HINTS_PATH)
    Call<ComboHintData> getAllHints();

    @GET(AppConstants.PROVINCES_PATH)
    Call<List<HintData>> getProvinces();

}
