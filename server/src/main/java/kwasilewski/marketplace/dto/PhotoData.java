package kwasilewski.marketplace.dto;

import javax.persistence.*;

@Entity
@Table(name = "PHOTOS")
@SequenceGenerator(name = "SEQ_PHT_ID", sequenceName = "SEQ_PHT_ID", allocationSize = 1)
public class PhotoData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PHT_ADS_ID", insertable = false, updatable = false)
    private AdData ad;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PHT_ID")
    @Column(name = "PHT_ID")
    private Long id;

    @Column(name = "PHT_ADS_ID")
    private Long adId;

    @Column(name = "PHT_MINIATURE")
    private boolean miniature = false;

    @Column(name = "PHT_PHOTO")
    @Lob
    private String photo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public boolean isMiniature() {
        return miniature;
    }

    public void setMiniature(boolean miniature) {
        this.miniature = miniature;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public AdData getAd() {
        return ad;
    }

    public void setAd(AdData ad) {
        this.ad = ad;
    }
}
