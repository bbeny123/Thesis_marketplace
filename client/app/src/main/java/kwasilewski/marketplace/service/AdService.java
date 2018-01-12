package kwasilewski.marketplace.service;

import java.util.List;
import java.util.Map;

import kwasilewski.marketplace.dto.ad.AdData;
import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.dto.ad.ListSearchData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AdService {

    @POST("/user/ads")
    Call<ResponseBody> newAd(@Header("token") String token, @Body AdData user);

    @GET("/user/ads")
    Call<List<AdMinimalData>> getUserAds(@Header("token") String token, Map<String, String> params);

    @GET("/user/ads/{id}")
    Call<AdDetailsData> getUserAd(@Header("token") String token, @Path("id") Long id);

    @PATCH("/user/ads/{id}")
    Call<ResponseBody> modifyUserAd(@Header("token") String token, @Path("id") Long id, @Body AdData ad);

    @PATCH("/user/ads/{id}/status")
    Call<ResponseBody> changeUserAdStaus(@Header("token") String token, @Path("id") Long id);

    @GET("/user/ads/{id}")
    Call<List<AdMinimalData>> getUserFavourites(@Header("token") String token, @Body ListSearchData search);

    @GET("/ads")
    Call<List<AdMinimalData>> getAds(Map<String, String> params);

    @GET("/ads/{id}")
    Call<AdDetailsData> getAd(@Path("id") Long id);

    @PATCH("/ads/{id}/favourite")
    Call<ResponseBody> addFavourite(@Header("token") String token, @Path("id") Long id);

    @DELETE("/ads/{id}/favourite")
    Call<ResponseBody> removeFavourite(@Header("token") String token, @Path("id") Long id);

}
