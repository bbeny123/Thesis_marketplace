package kwasilewski.marketplace.dtoext;

import kwasilewski.marketplace.dto.CategoryData;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryDataExt extends HintDataExt {

    private List<HintDataExt> subcategories;

    public CategoryDataExt(CategoryData cat) {
        super(cat);
        subcategories = cat.getSubcategories().stream().map(HintDataExt::new).collect(Collectors.toList());
    }

    public List<HintDataExt> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<HintDataExt> subcategories) {
        this.subcategories = subcategories;
    }

}
