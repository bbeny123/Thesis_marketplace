package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.dto.PhotoData;

public class PhotoResponse {

    private Long id;
    private String photo;

    public PhotoResponse(PhotoData photo) {
        this.id = photo.getId();
        this.photo = photo.getPhoto();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
