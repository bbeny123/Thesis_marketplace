package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.PhotoData;
import kwasilewski.marketplace.security.context.UserContext;
import kwasilewski.marketplace.util.AppConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdDataExt extends AdMinimalDataExt {

    protected Long catId;
    protected Long sctId;
    protected Long prvId;
    protected String description;
    protected String city;
    protected String phone;
    protected List<String> photos = new ArrayList<>();
    protected boolean active = true;

    public AdDataExt() {
    }

    AdDataExt(AdData ad) {
        super(ad);
        this.catId = ad.getCategory().getCategory().getId();
        this.sctId = ad.getCatId();
        this.prvId = ad.getPrvId();
        this.description = ad.getDescription();
        this.city = ad.getCity();
        this.phone = ad.getPhone();
        this.photos = ad.getPhotos().stream().map(PhotoData::getPhoto).collect(Collectors.toList());
        this.active = ad.isActive();
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AdData getAdData(UserContext ctx) {
        AdData ad = new AdData();
        ad.setUsrId(ctx.getUserId());
        ad.setCatId(this.sctId);
        ad.setPrvId(this.prvId);
        ad.setTitle(this.title);
        ad.setDescription(this.description);
        ad.setPrice(this.price);
        ad.setCity(this.city);
        ad.setPhone(this.phone);
        if(miniature != null) ad.setPhotos(getPhotosList());
        return ad;
    }

    public AdData getAdData(UserContext ctx, Long id) {
        AdData ad = getAdData(ctx);
        ad.setId(id);
        ad.setActive(active);
        return ad;
    }

    private List<PhotoData> getPhotosList() {
        List<PhotoData> photos = new ArrayList<>();
        photos.add(getPhotoData(miniature, true));
        this.photos.stream().limit(AppConstants.MAX_PHOTOS).forEach(photo -> photos.add(getPhotoData(photo, false)));
        return photos;
    }

    private PhotoData getPhotoData(String photo, boolean isMiniature) {
        PhotoData photoData = new PhotoData();
        photoData.setMiniature(isMiniature);
        photoData.setPhoto(photo);
        return photoData;
    }

}
