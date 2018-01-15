package kwasilewski.marketplace.retrofit.service;

import java.util.List;

import kwasilewski.marketplace.configuration.AppConstants;
import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.HintData;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HintService {

    @GET(AppConstants.PROVINCES_PATH)
    Call<List<HintData>> getProvinces();

    @GET(AppConstants.CATEGORIES_PATH)
    Call<List<CategoryData>> getCategories();

}
