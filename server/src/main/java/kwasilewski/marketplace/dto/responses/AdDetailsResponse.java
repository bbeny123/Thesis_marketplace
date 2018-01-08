package kwasilewski.marketplace.dto.responses;

import kwasilewski.marketplace.dto.AdData;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AdDetailsResponse extends AdResponse {

    private String userName;
    private String province;
    private Date date;
    private String description;
    private String city;
    private String phone;
    private List<PhotoResponse> photos;
    private Long views;

    public AdDetailsResponse(AdData ad) {
        super(ad);
        this.userName = ad.getUser().getFirstName();
        this.province = ad.getProvince().getName();
        this.date = ad.getDate();
        this.description = ad.getDescription();
        this.city = ad.getCity();
        this.phone = ad.getPhone();
        this.photos = ad.getPhotos().stream().map(PhotoResponse::new).collect(Collectors.toList());
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<PhotoResponse> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoResponse> photos) {
        this.photos = photos;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }
}
