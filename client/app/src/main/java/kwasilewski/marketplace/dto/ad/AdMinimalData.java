package kwasilewski.marketplace.dto.ad;

public class AdMinimalData {

    private Long id;
    private String title;
    private Long price;
    private String miniature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getMiniature() {
        return miniature;
    }

    public void setMiniature(String miniature) {
        this.miniature = miniature;
    }

//    @Override
//    public boolean equals(Object obj) {
//        return obj instanceof AdMinimalData && (obj == this || id.equals(((AdMinimalData) obj).getId()));
//    }
//
//    @Override
//    public int hashCode()
//    {
//        return id != null ? id.hashCode() : 0;
//    }

}
