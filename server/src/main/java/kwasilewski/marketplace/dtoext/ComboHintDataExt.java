package kwasilewski.marketplace.dtoext;

import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.ProvinceData;

import java.util.List;
import java.util.stream.Collectors;

public class ComboHintDataExt {

    private List<HintDataExt> provinces;
    private List<CategoryDataExt> categories;

    public ComboHintDataExt(List<ProvinceData> provinces, List<CategoryData> categories) {
        this.provinces = provinces.stream().map(HintDataExt::new).collect(Collectors.toList());
        this.categories = categories.stream().map(CategoryDataExt::new).collect(Collectors.toList());
    }

    public List<CategoryDataExt> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDataExt> categories) {
        this.categories = categories;
    }

    public List<HintDataExt> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<HintDataExt> provinces) {
        this.provinces = provinces;
    }

}
