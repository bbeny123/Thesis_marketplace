package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.dto.AdData;

import java.util.Date;

public class AdDetailsDataExt extends AdDataExt {

    private String userName;
    private String province;
    private Date date;
    private Long views;

    public AdDetailsDataExt(AdData ad) {
        super(ad);
        this.userName = ad.getUser().getFirstName();
        this.province = ad.getProvince().getName();
        this.date = ad.getDate();
        this.views = ad.getViews();
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }
}
