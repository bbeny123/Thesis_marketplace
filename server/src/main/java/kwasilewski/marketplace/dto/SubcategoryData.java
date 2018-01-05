package kwasilewski.marketplace.dto;

import javax.persistence.*;

@Entity
@Table(name = "SUBCATEGORIES")
@SequenceGenerator(name = "SEQ_SCT_ID", sequenceName = "SEQ_SCT_ID", allocationSize = 1)
public class SubcategoryData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCT_CAT_ID", insertable = false, updatable = false)
    private CategoryData category;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SCT_ID")
    @Column(name = "SCT_ID")
    private Long id;

    @Column(name = "SCT_NAME")
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

    public CategoryData getCategory() {
        return category;
    }

    public void setCategory(CategoryData category) {
        this.category = category;
    }
}