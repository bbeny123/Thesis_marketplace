package kwasilewski.marketplace.retrofit.service;

import java.util.List;
import java.util.Map;

import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.dto.ad.AdData;
import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface AdService {

    @POST(AppConstants.ADS_USER_PATH)
    Call<ResponseBody> createAd(@Header("token") String token, @Body AdData user);

    @PATCH(AppConstants.AD_USER_PATH)
    Call<ResponseBody> modifyAd(@Header("token") String token, @Path("id") Long id, @Body AdData ad);

    @PATCH(AppConstants.AD_STATUS_PATH)
    Call<ResponseBody> changeAdStatus(@Header("token") String token, @Path("id") Long id);

    @PATCH(AppConstants.AD_REFRESH_PATH)
    Call<ResponseBody> refreshAd(@Header("token") String token, @Path("id") Long id);

    @POST(AppConstants.FAVOURITE_PATH)
    Call<ResponseBody> addFavourite(@Header("token") String token, @Path("id") Long id);

    @DELETE(AppConstants.FAVOURITE_PATH)
    Call<ResponseBody> removeFavourite(@Header("token") String token, @Path("id") Long id);

    @GET(AppConstants.AD_PATH)
    Call<AdDetailsData> getAd(@Path("id") Long id);

    @GET(AppConstants.AD_USER_PATH)
    Call<AdDetailsData> getUserAd(@Header("token") String token, @Path("id") Long id);

    @GET(AppConstants.ADS_PATH)
    Call<List<AdMinimalData>> getAds(@QueryMap Map<String, String> params);

    @GET(AppConstants.ADS_USER_PATH)
    Call<List<AdMinimalData>> getUserAds(@Header("token") String token, @QueryMap Map<String, String> params);

    @GET(AppConstants.FAVOURITES_PATH)
    Call<List<AdMinimalData>> getFavourites(@Header("token") String token, @QueryMap Map<String, String> params);

}
