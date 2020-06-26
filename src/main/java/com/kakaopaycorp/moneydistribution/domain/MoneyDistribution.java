package com.kakaopaycorp.moneydistribution.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kakaopaycorp.moneydistribution.domain.exception.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
        for (int i = 0; i < pieceNum - 1; i++) {
            int money = random.nextInt(totalMoney + 1);
            moneyList.add(money);
            totalMoney -= money;
        }
        moneyList.add(totalMoney);

        Collections.shuffle(moneyList);
        return moneyList;
    }

    public MoneyPiece pickUnusedPiece(Account account) {

        validateToPick(account);

        MoneyPiece moneyPiece = this.getMoneyPieces().stream()
                .filter(piece -> !piece.isHasPicked())
                .findFirst().orElseThrow(EntityNotFoundException::new);

        moneyPiece.setHasPicked(true);
        moneyPiece.setPicker(account);
        moneyPiece.setDistributeAt(LocalDateTime.now());

        return moneyPiece;
    }

    private void validateToPick(Account account) {
        //10분 안넘었는지
        if (this.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(10)))
            throw new ValidTimeOverException();

        //account가 chatroom에 있는지
        if (!this.getChatRoom().containsAccount(account))
            throw new NotExistAccountAtChatRoomException(account, this.getChatRoom());

        //account가 이미 받았는지
        if (this.getMoneyPieces().stream()
                .anyMatch(moneyPiece ->
                    moneyPiece.getPicker() != null
                    && moneyPiece.getPicker().equals(account)
                ))
            throw new AccountAlreadyPickedException();

        //남은 piece 없음
        if (this.getMoneyPieces().stream()
                .filter(moneyPiece -> !moneyPiece.isHasPicked())
                .count() <= 0)
            throw new UnusedPieceNoneExistException();

        //뿌린 사람이랑 account랑 같은지
        if (this.getDistributor().equals(account))
            throw new DistributorCanNotPickException();
    }


}
