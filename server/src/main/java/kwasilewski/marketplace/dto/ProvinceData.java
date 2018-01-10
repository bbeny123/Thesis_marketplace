package kwasilewski.marketplace.dto;

import javax.persistence.*;

@Entity
@Table(name = "PROVINCES")
public class ProvinceData {

    @Id
    @Column(name = "PRV_ID")
    private Long id;

    @Column(name = "PRV_NAME")
    private String name;

    @Transient
    private Long usersNumber;

    @Transient
    private Long adsNumber;

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

    public Long getUsersNumber() {
        return usersNumber;
    }

    public void setUsersNumber(Long usersNumber) {
        this.usersNumber = usersNumber;
    }

    public Long getAdsNumber() {
        return adsNumber;
    }

    public void setAdsNumber(Long adsNumber) {
        this.adsNumber = adsNumber;
    }
}
