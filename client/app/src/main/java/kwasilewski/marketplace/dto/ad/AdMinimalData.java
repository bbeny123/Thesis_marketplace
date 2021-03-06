package kwasilewski.marketplace.dto.ad;

import android.util.Base64;

public class AdMinimalData {

    String title;
    String price;
    private Long id;
    private String views;
    private String miniature;
    private boolean refreshable;

    AdMinimalData() {
    }

    AdMinimalData(String title, String price, String miniature) {
        this.title = title;
        this.price = price;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
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

    public byte[] getDecodedMiniature() {
        if (miniature == null) {
            return null;
        }
        return Base64.decode(miniature, Base64.DEFAULT);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AdMinimalData && (obj == this || id.equals(((AdMinimalData) obj).getId()));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public interface SortingMethod {
        int NEWEST = 1;
        int CHEAPEST = 2;
        int MOSTEXPENSIVE = 3;
    }

}
