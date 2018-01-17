package kwasilewski.marketplace.dto;

public class HintData {

    private Long id;
    private String name;

    public HintData() {
    }

    public HintData(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HintData && (obj == this || id.equals(((HintData) obj).getId()));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
