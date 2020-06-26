package com.kakaopaycorp.moneydistribution.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
public class MoneyDistribution {

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn
    private Account distributor;

    @OneToMany(mappedBy = "moneyDistribution")
    @JsonManagedReference
    private List<MoneyPiece> moneyPieces;

    private LocalDateTime createdAt;


    public MoneyDistribution(ChatRoom chatRoom, Account account, int money, int pieceNum, String token) {
        this.chatRoom = chatRoom;
        this.distributor = account;
        this.moneyPieces = this.makeMoneyPiece(money, pieceNum);
        this.token = token;
        this.createdAt = LocalDateTime.now();
    }

    private List<MoneyPiece> makeMoneyPiece(int totalMoney, int pieceNum) {
        List<MoneyPiece> pieceSet = new ArrayList<>();

        this.makeRandomMoneyList(totalMoney, pieceNum)
                .forEach(money -> pieceSet.add(new MoneyPiece(this, money)));

        return pieceSet;
    }

    private List<Integer> makeRandomMoneyList(int totalMoney, int pieceNum) {
        List<Integer> moneyList = new ArrayList<>();

        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < pieceNum-1 ; i++) {
            int money = random.nextInt(totalMoney + 1);
            moneyList.add(money);
            totalMoney -= money;
        }
        moneyList.add(totalMoney);

        Collections.shuffle(moneyList);
        return moneyList;
    }
}
