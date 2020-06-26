package com.kakaopaycorp.moneydistribution.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
public class MoneyPiece {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn
    @JsonBackReference
    private MoneyDistribution moneyDistribution;

    private Integer moneyValue;

    private LocalDateTime distributeAt;

    @ManyToOne
    @JoinColumn
    private Account picker;

    private boolean hasPicked;

    public MoneyPiece(MoneyDistribution md, Integer money) {
        this.moneyDistribution = md;
        this.moneyValue = money;
        this.distributeAt = null;
        this.picker = null;
        this.hasPicked = false;
    }
}
