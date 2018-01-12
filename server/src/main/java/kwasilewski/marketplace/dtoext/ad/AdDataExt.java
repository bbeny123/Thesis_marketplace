package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.PhotoData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdDataExt extends AdMinimalDataExt {

    protected Long catId;
    protected Long prvId;
    protected String description;
    protected String city;
    protected String phone;
    protected List<String> photos;

    AdDataExt(AdData ad) {
        super(ad);
        this.catId = ad.getCatId();
        this.prvId = ad.getPrvId();
        this.description = ad.getDescription();
        this.city = ad.getCity();
        this.phone = ad.getPhone();
        this.photos = ad.getPhotos().stream().map(PhotoData::getPhoto).collect(Collectors.toList());
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

    public AdData getAdData(UserContext ctx) {
        AdData ad = new AdData();
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

    public AdData getAdData(UserContext ctx, Long id) {
        AdData ad = getAdData(ctx);
        ad.setId(id);
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
