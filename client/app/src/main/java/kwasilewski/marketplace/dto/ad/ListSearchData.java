package kwasilewski.marketplace.dto.ad;

import java.util.HashMap;
import java.util.Map;

public class ListSearchData {

    private int offset = 0;
    private int maxResults = 6;

    public ListSearchData() {
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

    public static Map<String, String> getSearchQuery(int offset, int maxResults) {
        Map<String, String> searchQuery = new HashMap<>();
        searchQuery.put("offset", String.valueOf(offset));
        searchQuery.put("maxResults", String.valueOf(maxResults));
        return searchQuery;
    }

}
