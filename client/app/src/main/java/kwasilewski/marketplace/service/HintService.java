package kwasilewski.marketplace.service;

import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.HintData;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HintService {

    @GET("/provinces")
    Call<HintData> getProvinces();

    @GET("/categories")
    Call<CategoryData> getCategories();

}
