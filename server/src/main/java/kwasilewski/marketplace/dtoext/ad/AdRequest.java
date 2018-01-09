package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.PhotoData;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class AdRequest {

    private Long id;
    @NotBlank
    private Long catId;
    @NotBlank
    private Long prvId;
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private Long price;
    @NotBlank
    private String city;
    @NotBlank
    private String phone;
    private String miniature;
    private List<String> photos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public Long getPrvId() {
        return prvId;
    }

    public void setPrvId(Long prvId) {
        this.prvId = prvId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
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

    public String getMiniature() {
        return miniature;
    }

    public void setMiniature(String miniature) {
        this.miniature = miniature;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public AdData getAdData(UserContext ctx) {
        AdData ad = new AdData();
        ad.setId(this.id);
        ad.setUsrId(ctx.getUserId());
        ad.setCatId(this.catId);
        ad.setPrvId(this.prvId);
        ad.setTitle(this.title);
        ad.setDescription(this.description);
        ad.setPrice(this.price);
        ad.setCity(this.city);
        ad.setPhone(this.phone);
        ad.setPhotos(getPhotosList());
        return ad;
    }

    private List<PhotoData> getPhotosList() {
        List<PhotoData> photos = new ArrayList<>();
        photos.add(getPhotoData(miniature, true));
        this.photos.forEach(photo -> photos.add(getPhotoData(photo, false)));
        return photos;
    }

    private PhotoData getPhotoData(String photo, boolean isMiniature) {
        PhotoData photoData = new PhotoData();
        photoData.setMiniature(isMiniature);
        photoData.setPhoto(photo);
        return photoData;
    }
}
