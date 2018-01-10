package kwasilewski.marketplace.dto;

import javax.persistence.*;

@Entity
@Table(name = "CURRENCIES")
@SequenceGenerator(name = "SEQ_CUR_ID", sequenceName = "SEQ_CUR_ID", allocationSize = 1)
public class CurrencyData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CUR_ID")
    @Column(name = "CUR_ID")
    private Long id;

    @Column(name = "CUR_SYMBOL")
    private String symbol;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
