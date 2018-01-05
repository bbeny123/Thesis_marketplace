package kwasilewski.marketplace.dto;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TOKENS")
@SequenceGenerator(name = "SEQ_TKN_ID", sequenceName = "SEQ_TKN_ID", allocationSize = 1)
public class TokenData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TKN_USR_ID", insertable = false, updatable = false)
    private UserData user;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TKN_ID")
    @Column(name = "TKN_ID")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TKN_DATE")
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }
}
