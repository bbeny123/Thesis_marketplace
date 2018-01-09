package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.dto.AdData;

public class AdResponse {
    private Long id;
    private String title;
    private Long price;
    private String miniature;

    public AdResponse(AdData ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.price = ad.getPrice();
        this.miniature = ad.getMiniature() != null ? ad.getMiniature().getPhoto() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getMiniature() {
        return miniature;
    }

    public void setMiniature(String miniature) {
        this.miniature = miniature;
    }
}
