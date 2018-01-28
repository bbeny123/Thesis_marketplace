package kwasilewski.marketplace.dto.ad;

import android.util.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdData extends AdMinimalData {

    private Long catId;
    private Long sctId;
    private Long prvId;
    private String description;
    private String city;
    private String phone;
    private List<String> photos = new ArrayList<>();
    private boolean active = true;

    AdData() {
    }

    public AdData(String title, String price, Long sctId, Long prvId, String description, String city, String phone, String miniature, List<String> photos) {
        super(title, price, miniature);
        this.sctId = sctId;
        this.prvId = prvId;
        this.description = description;
        this.city = city;
        this.phone = phone;
        this.photos = photos;
    }

    public AdData(String title, String price, Long sctId, Long prvId, String description, String city, String phone, boolean active) {
        super(title, price);
        this.sctId = sctId;
        this.prvId = prvId;
        this.description = description;
        this.city = city;
        this.phone = phone;
        this.active = active;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public Long getSctId() {
        return sctId;
    }

    public void setSctId(Long sctId) {
        this.sctId = sctId;
    }

    public Long getPrvId() {
        return prvId;
    }

    public void setPrvId(Long prvId) {
        this.prvId = prvId;
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

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<byte[]> getDecodedPhotos() {
        return photos.stream().map(photo -> Base64.decode(photo, Base64.DEFAULT)).collect(Collectors.toList());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void update(String title, String price, Long sctId, Long prvId, String description, String city, String phone, boolean active) {
        this.title = title;
        this.price = price;
        this.sctId = sctId;
        this.prvId = prvId;
        this.description = description;
        this.city = city;
        this.phone = phone;
        this.active = active;
    }
}
