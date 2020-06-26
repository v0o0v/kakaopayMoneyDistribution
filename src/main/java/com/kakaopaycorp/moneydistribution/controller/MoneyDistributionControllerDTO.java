package com.kakaopaycorp.moneydistribution.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class MoneyDistributionControllerDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequestDTO {

        @Min(value = 0, message = "뿌리기 지정 액수는 0보디 작을 수 없습니다.")
        private Integer money;

        @Min(value = 1, message = "뿌리기 지정 대상 인원은 1보다 작을 수 없습니다.")
        private Integer pieceNum;

        public CreateRequestDTO(Integer money, Integer pieceNum) {
            this.money = money;
            this.pieceNum = pieceNum;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateResponseDTO {

        private String token;

        public CreateResponseDTO(String token) {
            this.token = token;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class PickRequestDTO {

        @NotEmpty(message = "Token 값이 비어있습니다.")
        private String token;

        public PickRequestDTO(String token) {
            this.token = token;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PickResponseDTO {

        private Integer pickedMoneyValue;

        public PickResponseDTO(Integer pickedMoneyValue) {
            this.pickedMoneyValue = pickedMoneyValue;
        }
    }
}
