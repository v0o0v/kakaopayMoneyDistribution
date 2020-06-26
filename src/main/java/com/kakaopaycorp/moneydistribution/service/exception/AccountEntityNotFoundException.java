package com.kakaopaycorp.moneydistribution.service.exception;

public class AccountEntityNotFoundException extends RuntimeException {
    public AccountEntityNotFoundException() {
        super("Account Entity를 찾을 수 없습니다.");
    }
}
