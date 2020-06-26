package com.kakaopaycorp.moneydistribution.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;

public class MoneyDistributionControllerDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MoneyDistributionCreateDTO {

        @Min(0)
        private Integer money;

        @Min(0)
        private Integer pieceNum;

        public MoneyDistributionCreateDTO(@Min(0) Integer money, @Min(0) Integer pieceNum) {
            this.money = money;
            this.pieceNum = pieceNum;
        }
    }
}
