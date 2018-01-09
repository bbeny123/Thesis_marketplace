package kwasilewski.marketplace.dtoext;

import kwasilewski.marketplace.dto.CategoryData;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryResponse extends ShortResponse {

    private List<ShortResponse> subcategories;

    public CategoryResponse(CategoryData cat) {
        super(cat);
        subcategories = cat.getSubcategories().stream().map(ShortResponse::new).collect(Collectors.toList());
    }

    public List<ShortResponse> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<ShortResponse> subcategories) {
        this.subcategories = subcategories;
    }
}
