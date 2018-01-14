package kwasilewski.marketplace.configuration;

public final class AppConstants {

    private AppConstants() {
    }

    public static final String BASE_URL = "http://10.0.2.2:8080/rest/";
    public static final String PROVINCES_PATH = "provinces";
    public static final String CATEGORIES_PATH = "categories";
    public static final String REGISTER_PATH = "register";
    public static final String LOGIN_PATH = "login";
    public static final String TOKEN_PATH = "token";
    public static final String USER_PATH = "user/{id}";
    public static final String USER_ADS_PATH = "user/ads";
    public static final String USER_AD_PATH = "user/ads/{id}";
    public static final String USER_AD_STATUS_PATH = "user/ads/{id}/status";
    public static final String USER_FAVOURITES_PATH = "user/favourites";
    public static final String ADS_PATH = "ads";
    public static final String AD_PATH = "ads/{id}";
    public static final String FAVOURITE_PATH = "ads/{id}/favourite";

}