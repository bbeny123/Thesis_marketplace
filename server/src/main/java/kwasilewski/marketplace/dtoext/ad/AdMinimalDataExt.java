package kwasilewski.marketplace.dtoext.ad;

import kwasilewski.marketplace.dto.AdData;

public class AdMinimalDataExt {

    protected Long id;
    protected String title;
    protected Long price;
    protected Long views;
    protected String miniature;

    AdMinimalDataExt() {
    }

    public AdMinimalDataExt(AdData ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.price = ad.getPrice();
        this.views = ad.getViews();
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

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public String getMiniature() {
        return miniature;
    }

    public void setMiniature(String miniature) {
        this.miniature = miniature;
    }

}
