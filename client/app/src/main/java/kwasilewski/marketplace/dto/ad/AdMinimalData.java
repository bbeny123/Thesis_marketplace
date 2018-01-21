package kwasilewski.marketplace.dto.ad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class AdMinimalData {

    private Long id;
    private String title;
    private Long price;
    private Long views;
    private String miniature;
    protected boolean refreshable;

    public AdMinimalData() {
    }

    public AdMinimalData(String title, Long price, String miniature) {
        this.title = title;
        this.price = price;
        this.miniature = miniature;
    }

    public AdMinimalData(Long id, String title, Long price, Long views, String miniature) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.views = views;
        this.miniature = miniature;
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

    public boolean isRefreshable() {
        return refreshable;
    }

    public void setRefreshable(boolean refreshable) {
        this.refreshable = refreshable;
    }

    public Bitmap getDecodedMiniature() {
        if (miniature == null) {
            return null;
        }
        byte[] decodedMiniature = Base64.decode(miniature, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedMiniature, 0, decodedMiniature.length);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AdMinimalData && (obj == this || id.equals(((AdMinimalData) obj).getId()));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
