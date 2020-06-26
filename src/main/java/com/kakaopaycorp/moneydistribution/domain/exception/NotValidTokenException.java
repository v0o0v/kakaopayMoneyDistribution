package com.kakaopaycorp.moneydistribution.domain.exception;

public class NotValidTokenException extends RuntimeException {

    public NotValidTokenException() {
        super("Token이 유효하지 않습니다.");
    }
}
