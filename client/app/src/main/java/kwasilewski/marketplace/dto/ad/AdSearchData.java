package kwasilewski.marketplace.dto.ad;

public class AdSearchData extends ListSearchData {

    private int sortingMethod = 1;
    private String title;
    private Long prvId;
    private Long catId;
    private Long priceMin;
    private Long priceMax;
    private boolean active = true; // only for user's ads

    public AdSearchData() {
    }

    public int getSortingMethod() {
        return sortingMethod;
    }

    public void setSortingMethod(int sortingMethod) {
        this.sortingMethod = sortingMethod;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPrvId() {
        return prvId;
    }

    public void setPrvId(Long prvId) {
        this.prvId = prvId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public Long getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(Long priceMin) {
        this.priceMin = priceMin;
    }

    public Long getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(Long priceMax) {
        this.priceMax = priceMax;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
