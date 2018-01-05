package kwasilewski.marketplace.dto;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "CATEGORIES")
@SequenceGenerator(name = "SEQ_CAT_ID", sequenceName = "SEQ_CAT_ID", allocationSize = 1)
public class CategoryData {

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SubcategoryData> subcategories;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CAT_ID")
    @Column(name = "CAT_ID")
    private Long id;

    @Column(name = "CAT_NAME")
    private String name;

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

    public List<SubcategoryData> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<SubcategoryData> subcategories) {
        this.subcategories = subcategories;
    }
}
