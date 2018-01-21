package kwasilewski.marketplace.retrofit.service;

import java.util.List;
import java.util.Map;

import kwasilewski.marketplace.configuration.AppConstants;
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

    @POST(AppConstants.USER_ADS_PATH)
    Call<ResponseBody> newAd(@Header("token") String token, @Body AdData user);

    @GET(AppConstants.USER_ADS_PATH)
    Call<List<AdMinimalData>> getUserAds(@Header("token") String token, @QueryMap Map<String, String> params);

    @GET(AppConstants.USER_AD_PATH)
    Call<AdDetailsData> getUserAd(@Header("token") String token, @Path("id") Long id);

    @PATCH(AppConstants.USER_AD_PATH)
    Call<ResponseBody> modifyUserAd(@Header("token") String token, @Path("id") Long id, @Body AdData ad);

    @PATCH(AppConstants.USER_AD_STATUS_PATH)
    Call<ResponseBody> changeUserAdStaus(@Header("token") String token, @Path("id") Long id);

    @GET(AppConstants.USER_FAVOURITES_PATH)
    Call<List<AdMinimalData>> getUserFavourites(@Header("token") String token, @QueryMap Map<String, String> params);

    @GET(AppConstants.ADS_PATH)
    Call<List<AdMinimalData>> getAds(@QueryMap Map<String, String> params);

    @GET(AppConstants.AD_PATH)
    Call<AdDetailsData> getAd(@Path("id") Long id);

    @POST(AppConstants.FAVOURITE_PATH)
    Call<ResponseBody> addFavourite(@Header("token") String token, @Path("id") Long id);

    @DELETE(AppConstants.FAVOURITE_PATH)
    Call<ResponseBody> removeFavourite(@Header("token") String token, @Path("id") Long id);

}
