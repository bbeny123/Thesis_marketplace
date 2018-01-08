package kwasilewski.marketplace.dto;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ADS")
@SequenceGenerator(name = "SEQ_ADS_ID", sequenceName = "SEQ_ADS_ID", allocationSize = 1)
public class AdData {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADS_CAT_ID", insertable = false, updatable = false)
    private SubcategoryData category;

    @OneToMany(mappedBy = "ad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PhotoData> photos;

    @OneToMany(mappedBy = "ad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FavouriteData> favourites;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADS_USR_ID", insertable = false, updatable = false)
    private UserData user;
    @Column(name = "ADS_USR_ID")
    private Long usrId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADS_CUR_ID", insertable = false, updatable = false)
    private CurrencyData currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADS_PRV_ID", insertable = false, updatable = false)
    private ProvinceData province;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ADS_ID")
    @Column(name = "ADS_ID")
    private Long id;
    @Transient
    private PhotoData miniature;

    @Column(name = "ADS_CAT_ID")
    private Long catId;

    @Column(name = "ADS_PRV_ID")
    private Long prvId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ADS_DATE")
    private Date date;

    @Column(name = "ADS_TITLE")
    private String title;

    @Column(name = "ADS_DESCRIPTION")
    private String description;

    @Column(name = "ADS_PRICE")
    private Long price;

    @Column(name = "ADS_CITY")
    private String city;

    @Column(name = "ADS_PHONE")
    private String phone;

    @Column(name = "ADS_VIEWS")
    private Long views;

    @Column(name = "ADS_ACTIVE")
    private boolean active;

    public static String getSortingMethod(int sortingMethod) {
        switch (sortingMethod) {
            case SortingMethod.CHEAPEST:
                return "price";
            case SortingMethod.MOSTEXPENSIVE:
                return "price DESC";
            case SortingMethod.NEWEST:
            default:
                return "date DESC";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsrId() {
        return usrId;
    }

    public void setUsrId(Long usrId) {
        this.usrId = usrId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public Long getPrvId() {
        return prvId;
    }

    public void setPrvId(Long prvId) {
        this.prvId = prvId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public PhotoData getMiniature() {
        return miniature;
    }

    public void setMiniature(PhotoData miniature) {
        this.miniature = miniature;
    }

    public List<PhotoData> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoData> photos) {
        this.photos = photos;
    }

    public List<FavouriteData> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<FavouriteData> favourites) {
        this.favourites = favourites;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public SubcategoryData getCategory() {
        return category;
    }

    public void setCategory(SubcategoryData category) {
        this.category = category;
    }

    public CurrencyData getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyData currency) {
        this.currency = currency;
    }

    public ProvinceData getProvince() {
        return province;
    }

    public void setProvince(ProvinceData province) {
        this.province = province;
    }

    public interface SortingMethod {
        int NEWEST = 1;
        int CHEAPEST = 2;
        int MOSTEXPENSIVE = 3;
    }

}
