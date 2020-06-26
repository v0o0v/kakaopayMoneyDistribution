package com.kakaopaycorp.moneydistribution.domain.exception;

public class MoneyDistributeAccessValidationException extends RuntimeException {

    public MoneyDistributeAccessValidationException() {
        super("자신의 뿌리기만 접근 가능합니다.");
    }
}
