package kwasilewski.marketplace.dtoext;

public class ConfigResponse {

    private String baseUrl = "http://localhost:8080/rest";
    private String provincesPath = "/provinces";
    private String categoriesPath = "/categories";
    private String registerPath = "/register";
    private String loginPath = "/login";
    private String tokenPath = "/token";
    private String userPath = "/user/{id}";
    private String userAdsPath = "/user/ads";
    private String userAdPath = "/user/ads/{id}";
    private String userAdStatusPath = "/user/ads/{id}/status";
    private String userFavouritesPath = "/user/favourites";
    private String adsPath = "/ads";
    private String adPath = "/ads/{id}";
    private String favouritePath = "/ads/{id}/favourite";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getProvincesPath() {
        return provincesPath;
    }

    public void setProvincesPath(String provincesPath) {
        this.provincesPath = provincesPath;
    }

    public String getCategoriesPath() {
        return categoriesPath;
    }

    public void setCategoriesPath(String categoriesPath) {
        this.categoriesPath = categoriesPath;
    }

    public String getRegisterPath() {
        return registerPath;
    }

    public void setRegisterPath(String registerPath) {
        this.registerPath = registerPath;
    }

    public String getLoginPath() {
        return loginPath;
    }

    public void setLoginPath(String loginPath) {
        this.loginPath = loginPath;
    }

    public String getTokenPath() {
        return tokenPath;
    }

    public void setTokenPath(String tokenPath) {
        this.tokenPath = tokenPath;
    }

    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }

    public String getUserAdsPath() {
        return userAdsPath;
    }

    public void setUserAdsPath(String userAdsPath) {
        this.userAdsPath = userAdsPath;
    }

    public String getUserAdPath() {
        return userAdPath;
    }

    public void setUserAdPath(String userAdPath) {
        this.userAdPath = userAdPath;
    }

    public String getUserAdStatusPath() {
        return userAdStatusPath;
    }

    public void setUserAdStatusPath(String userAdStatusPath) {
        this.userAdStatusPath = userAdStatusPath;
    }

    public String getUserFavouritesPath() {
        return userFavouritesPath;
    }

    public void setUserFavouritesPath(String userFavouritesPath) {
        this.userFavouritesPath = userFavouritesPath;
    }

    public String getAdsPath() {
        return adsPath;
    }

    public void setAdsPath(String adsPath) {
        this.adsPath = adsPath;
    }

    public String getAdPath() {
        return adPath;
    }

    public void setAdPath(String adPath) {
        this.adPath = adPath;
    }

    public String getFavouritePath() {
        return favouritePath;
    }

    public void setFavouritePath(String favouritePath) {
        this.favouritePath = favouritePath;
    }

}
