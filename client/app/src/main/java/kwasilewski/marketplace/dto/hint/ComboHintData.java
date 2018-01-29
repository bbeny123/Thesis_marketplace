package kwasilewski.marketplace.dto.hint;

import java.util.List;

public class ComboHintData {

    private List<HintData> provinces;
    private List<CategoryData> categories;

    public ComboHintData() {
    }

    public List<HintData> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<HintData> provinces) {
        this.provinces = provinces;
    }

    public List<CategoryData> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryData> categories) {
        this.categories = categories;
    }

}
