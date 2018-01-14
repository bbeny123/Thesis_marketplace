package kwasilewski.marketplace.retrofit.service;

import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.HintData;
import kwasilewski.marketplace.configuration.AppConstants;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HintService {

    @GET(AppConstants.PROVINCES_PATH)
    Call<HintData> getProvinces();

    @GET(AppConstants.CATEGORIES_PATH)
    Call<CategoryData> getCategories();

}
