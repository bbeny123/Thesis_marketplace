package kwasilewski.marketplace.retrofit.listener;

import java.util.List;

import kwasilewski.marketplace.dto.ad.AdDetailsData;
import kwasilewski.marketplace.dto.ad.AdMinimalData;

public interface AdListener {

    default void adCreated() {
    }

    default void getUserAds(List<AdMinimalData> ads) {
    }

    default void getUserAd(AdDetailsData ad) {
    }

    default void modifyUserAd() {
    }

    default void changeUserAdStatus() {
    }

    default void refreshAd() {
    }

    default void getUserFavourites(List<AdMinimalData> ads) {
    }

    default void getAds(List<AdMinimalData> ads) {
    }

    default void getAd(AdDetailsData ad) {
    }

    default void addFavourite() {
    }

    default void removeFavourite() {
    }

}
