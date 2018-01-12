package kwasilewski.marketplace.dtoext;

import kwasilewski.marketplace.dto.CategoryData;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryResponse extends HintResponse {

    private List<HintResponse> subcategories;

    public CategoryResponse(CategoryData cat) {
        super(cat);
        subcategories = cat.getSubcategories().stream().map(HintResponse::new).collect(Collectors.toList());
    }

    public List<HintResponse> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<HintResponse> subcategories) {
        this.subcategories = subcategories;
    }

}
