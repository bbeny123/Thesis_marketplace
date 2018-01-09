package kwasilewski.marketplace.dtoext;

public class ListRequest {

    private int offset = 0;
    private int maxResults = 5;

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

}
