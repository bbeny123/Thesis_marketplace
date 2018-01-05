package kwasilewski.marketplace.dto;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USERS")
@SequenceGenerator(name = "SEQ_USR_ID", sequenceName = "SEQ_USR_ID", allocationSize = 1)
public class UserData {

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TokenData> tokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AdData> ads;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FavouriteData> favourites;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USR_PRV_ID", insertable = false, updatable = false)
    private ProvinceData province;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USR_ID")
    @Column(name = "USR_ID")
    private Long id;

    @Column(name = "USR_EMAIL")
    private String email;

    @Column(name = "USR_PASSWORD")
    private String password;

    @Column(name = "USR_FIRST_NAME")
    private String firstName;

    @Column(name = "USR_LAST_NAME")
    private String lastName;

    @Column(name = "USR_CITY")
    private String city;

    @Column(name = "USR_PHONE")
    private String phone;

    @Column(name = "USR_AVATAR")
    @Lob
    private String avatar;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "USR_AVATAR_DATE")
    private Date avatarDate;

    @Column(name = "USR_ADMIN")
    private boolean admin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getAvatarDate() {
        return avatarDate;
    }

    public void setAvatarDate(Date avatarDate) {
        this.avatarDate = avatarDate;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public ProvinceData getProvince() {
        return province;
    }

    public void setProvince(ProvinceData province) {
        this.province = province;
    }

    public List<TokenData> getTokens() {
        return tokens;
    }

    public void setTokens(List<TokenData> tokens) {
        this.tokens = tokens;
    }

    public List<AdData> getAds() {
        return ads;
    }

    public void setAds(List<AdData> ads) {
        this.ads = ads;
    }

    public List<FavouriteData> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<FavouriteData> favourites) {
        this.favourites = favourites;
    }

    public String getRole() {
        if (isAdmin()) {
            return "ADMIN";
        } else {
            return "USER";
        }
    }

}
