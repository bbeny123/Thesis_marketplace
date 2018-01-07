package kwasilewski.marketplace.responses;

public class AdSearchRequest {

    private int sortingMethod = 1;
    private int offset = 0;
    private int maxResults = 5;
    private String title;
    private Long prvId;
    private Long catId;
    private Long priceMin;
    private Long priceMax;

    public int getSortingMethod() {
        return sortingMethod;
    }

    public void setSortingMethod(int sortingMethod) {
        this.sortingMethod = sortingMethod;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
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

}
