package kwasilewski.marketplace.dto;

import javax.persistence.*;

@Entity
@Table(name = "FAVOURITES")
@SequenceGenerator(name = "SEQ_FAV_ID", sequenceName = "SEQ_FAV_ID", allocationSize = 1)
public class FavouriteData {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FAV_USR_ID", insertable = false, updatable = false)
    private UserData user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FAV_ADS_ID", insertable = false, updatable = false)
    private AdData ad;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FAV_ID")
    @Column(name = "FAV_ID")
    private Long id;

    @Column(name = "FAV_USR_ID")
    private Long usrId;

    @Column(name = "FAV_ADS_ID")
    private Long adId;

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

    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public AdData getAd() {
        return ad;
    }

    public void setAd(AdData ad) {
        this.ad = ad;
    }
}
