package com.kakaopaycorp.moneydistribution.service.exception;

public class MoneyCanNotBeMinusException extends RuntimeException {

    public MoneyCanNotBeMinusException() {
        super("분배 금액은 0보다 작을 수 없습니다.");
    }
}
