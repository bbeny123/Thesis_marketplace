package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.dto.AdData;

public class AdDetailsDataExt extends AdDataExt {

    private String email;
    private String userName;
    private String province;
    private boolean favourite;

    public AdDetailsDataExt(AdData ad) {
        super(ad);
        this.email = ad.getUser().getEmail();
        this.userName = ad.getUser().getFirstName();
        this.province = ad.getProvince().getName();
        this.favourite = ad.isFavourite();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

}
