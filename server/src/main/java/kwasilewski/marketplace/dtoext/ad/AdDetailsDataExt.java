package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.dto.AdData;

public class AdDetailsDataExt extends AdDataExt {

    private String email;
    private String userName;
    private String province;
    private String category;
    private String subcategory;
    private boolean favourite;
    private boolean owner;

    public AdDetailsDataExt(AdData ad) {
        super(ad);
        this.email = ad.getUser().getEmail();
        this.userName = ad.getUser().getFirstName();
        this.province = ad.getProvince().getName();
        this.category = ad.getCategory().getCategory().getName();
        this.subcategory = ad.getCategory().getName();
        this.favourite = ad.isFavourite();
        this.owner = ad.isOwner();
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }
}
