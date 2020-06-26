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
}