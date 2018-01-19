package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.dto.AdData;

public class AdDetailsDataExt extends AdDataExt {

    private String email;
    private String userName;
    private String province;
    private Long views;

    public AdDetailsDataExt(AdData ad) {
        super(ad);
        this.email = ad.getUser().getEmail();
        this.userName = ad.getUser().getFirstName();
        this.province = ad.getProvince().getName();
        this.views = ad.getViews();
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

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }
}
