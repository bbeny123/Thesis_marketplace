package kwasilewski.marketplace.retrofit.manager;

import android.app.Activity;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import kwasilewski.marketplace.dto.ad.AdData;
import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.retrofit.RetrofitCallback;
import kwasilewski.marketplace.retrofit.RetrofitService;
import kwasilewski.marketplace.retrofit.listener.AdListener;
import kwasilewski.marketplace.retrofit.listener.ErrorListener;
import kwasilewski.marketplace.retrofit.service.AdService;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.SharedPrefUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class AdManager {

    private final static AdService adService = RetrofitService.getInstance().getAdService();
    private final Activity activity;
    private final AdListener adListener;
    private Call<ResponseBody> call;
    private Call<AdDetailsData> callAd;
    private Call<List<AdMinimalData>> callList;
    private String token;

    public AdManager(Activity activity, AdListener adListener) {
        this.activity = activity;
        this.adListener = adListener;
        this.token = SharedPrefUtil.getInstance(activity).getToken();
    }

    private static Map<String, String> getFavouriteAdSearchQuery(int offset) {
        Map<String, String> searchQuery = new HashMap<>();
        searchQuery.put("offset", String.valueOf(offset));
        searchQuery.put("maxResults", String.valueOf(AppConstants.MAX_RESULTS));
        return searchQuery;
    }

    private static Map<String, String> getUserAdSearchQuery(int offset, boolean active) {
        Map<String, String> searchQuery = getFavouriteAdSearchQuery(offset);
        if (!active) {
            searchQuery.put("active", "false");
        }
        return searchQuery;
    }

    private static Map<String, String> getAdSearchQuery(int offset, int sortingMethod, String title, Long prvId, Long catId, String priceMin, String priceMax) {
        Map<String, String> searchQuery = getFavouriteAdSearchQuery(offset);
        searchQuery.put("sortingMethod", String.valueOf(sortingMethod));
        if (!TextUtils.isEmpty(title)) {
            searchQuery.put("title", title);
        }
        if (prvId != null && prvId != 0) {
            searchQuery.put("prvId", String.valueOf(prvId));
        }
        if (catId != null && catId != 0) {
            searchQuery.put("catId", String.valueOf(catId));
        }
        if (!TextUtils.isEmpty(priceMin)) {
            searchQuery.put("priceMin", priceMin);
        }
        if (!TextUtils.isEmpty(priceMax)) {
            searchQuery.put("priceMax", priceMax);
        }
        return searchQuery;
    }

    public void createAd(AdData ad, ErrorListener errorListener) {
        if (token == null) {
            return;
        }
        call = adService.createAd(token, ad);
        call.enqueue(getRetrofitCallback(adListener::adCreated, errorListener));
    }

    public void modifyAd(Long id, AdData ad, ErrorListener errorListener) {
        if (token == null) {
            return;
        }
        call = adService.modifyAd(token, id, ad);
        call.enqueue(getRetrofitCallback(adListener::adModified, errorListener));
    }

    public void changeAdStatus(Long id, ErrorListener errorListener) {
        if (token == null) {
            return;
        }
        call = adService.changeAdStatus(token, id);
        call.enqueue(getRetrofitCallback(adListener::adStatusChanged, errorListener));
    }

    public void refreshAd(Long id, ErrorListener errorListener) {
        if (token == null) {
            return;
        }
        call = adService.refreshAd(token, id);
        call.enqueue(getRetrofitCallback(adListener::adRefreshed, errorListener));
    }

    public void addFavourite(Long id, ErrorListener errorListener) {
        if (token == null) {
            return;
        }
        call = adService.addFavourite(token, id);
        call.enqueue(getRetrofitCallback(adListener::favouriteAdded, errorListener));
    }

    public void removeFavourite(Long id, ErrorListener errorListener) {
        if (token == null) {
            return;
        }
        call = adService.removeFavourite(token, id);
        call.enqueue(getRetrofitCallback(adListener::favouriteRemoved, errorListener));
    }

    public void pullAd(Long id, ErrorListener errorListener) {
        if (token == null) {
            pullGuestAd(id, errorListener);
        } else {
            callAd = adService.getUserAd(token, id);
            callAd.enqueue(getRetrofitCallback(adListener::adReceived, errorListener));
        }
    }

    private void pullGuestAd(Long id, ErrorListener errorListener) {
        callAd = adService.getAd(id);
        callAd.enqueue(getRetrofitCallback(adListener::adReceived, errorListener));
    }

    public void pullAds(int offset, int sortingMethod, String title, Long prvId, Long catId, String priceMin, String priceMax, ErrorListener errorListener) {
        callList = adService.getAds(getAdSearchQuery(offset, sortingMethod, title, prvId, catId, priceMin, priceMax));
        callList.enqueue(getRetrofitCallback(adListener::adsReceived, errorListener));
    }

    public void pullUserAds(int offset, boolean active, ErrorListener errorListener) {
        if (token == null) {
            return;
        }
        callList = adService.getUserAds(token, getUserAdSearchQuery(offset, active));
        callList.enqueue(getRetrofitCallback(adListener::adsReceived, errorListener));
    }

    public void pullFavourites(int offset, ErrorListener errorListener) {
        if (token == null) {
            return;
        }
        callList = adService.getFavourites(token, getFavouriteAdSearchQuery(offset));
        callList.enqueue(getRetrofitCallback(adListener::adsReceived, errorListener));
    }

    public void cancelCalls() {
        if (call != null) {
            call.cancel();
        }
        if (callAd != null) {
            callAd.cancel();
        }
        if (callList != null) {
            callList.cancel();
        }
    }

    public void updateToken() {
        this.token = SharedPrefUtil.getInstance(activity).getToken();
    }

    private <T> RetrofitCallback<T> getRetrofitCallback(Consumer<T> function, ErrorListener errorListener) {
        return new RetrofitCallback<>(function, activity, errorListener);
    }

}
