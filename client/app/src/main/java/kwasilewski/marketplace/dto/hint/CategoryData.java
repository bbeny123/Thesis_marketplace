package kwasilewski.marketplace.dto.hint;

import java.util.List;

public class CategoryData extends HintData {

    private List<HintData> subcategories;

    public CategoryData() {
    }

    public List<HintData> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<HintData> subcategories) {
        this.subcategories = subcategories;
    }

}
