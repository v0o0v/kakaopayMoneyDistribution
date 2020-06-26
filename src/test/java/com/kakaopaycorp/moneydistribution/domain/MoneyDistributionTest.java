package com.kakaopaycorp.moneydistribution.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MoneyDistributionTest {


    @Test
    public void MoneyDistribution생성() {
        int totalMoney = 100;
        int pieceNum = 3;

        MoneyDistribution md = new MoneyDistribution(null, null, totalMoney, pieceNum, null);

        //조각갯수
        assertThat(md.getMoneyPieces().size()).isEqualTo(pieceNum);

        //머니값분배
        assertThat(md.getMoneyPieces().stream()
                .mapToInt(MoneyPiece::getMoneyValue)
                .sum())
                .isEqualTo(totalMoney);
    }

    @Test
    public void getPickedMoneySum() {
        int totalMoney = 100;
        int pieceNum = 3;

        MoneyDistribution md = new MoneyDistribution(null, null, totalMoney, pieceNum, null);

        assertThat(md.getPickedMoneySum()).isEqualTo(0);
        MoneyPiece moneyPiece = md.getMoneyPieces().get(0);
        moneyPiece.setHasPicked(true);
        assertThat(md.getPickedMoneySum()).isEqualTo(moneyPiece.getMoneyValue());
    }

    @Test
    public void getUsedPieces() {
        int totalMoney = 100;
        int pieceNum = 3;

        MoneyDistribution md = new MoneyDistribution(null, null, totalMoney, pieceNum, null);

        assertThat(md.getUsedPieces().size()).isEqualTo(0);
        md.getMoneyPieces().get(0).setHasPicked(true);
        assertThat(md.getUsedPieces().size()).isEqualTo(1);
    }
}