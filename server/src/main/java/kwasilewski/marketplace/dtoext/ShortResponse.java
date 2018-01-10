package kwasilewski.marketplace.dtoext;

import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.ProvinceData;
import kwasilewski.marketplace.dto.SubcategoryData;

public class ShortResponse {

    private Long id;
    private String name;

    public ShortResponse(ProvinceData prv) {
        this.id = prv.getId();
        this.name = prv.getName();
    }

    public ShortResponse(CategoryData cat) {
        this.id = cat.getId();
        this.name = cat.getName();
    }

    public ShortResponse(SubcategoryData cat) {
        this.id = cat.getId();
        this.name = cat.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
