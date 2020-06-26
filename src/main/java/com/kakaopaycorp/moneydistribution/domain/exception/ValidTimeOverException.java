package com.kakaopaycorp.moneydistribution.domain.exception;

public class ValidTimeOverException extends RuntimeException {

    public ValidTimeOverException() {
        super("Token이 유효하지 않습니다.");
    }
}
