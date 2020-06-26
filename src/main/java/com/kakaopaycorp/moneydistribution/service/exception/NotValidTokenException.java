package com.kakaopaycorp.moneydistribution.service.exception;

public class NotValidTokenException extends RuntimeException {

    public NotValidTokenException() {
        super("Token이 유효하지 않습니다.");
    }
}
