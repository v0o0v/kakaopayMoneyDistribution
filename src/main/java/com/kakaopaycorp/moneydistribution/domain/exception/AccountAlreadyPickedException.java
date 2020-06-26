package com.kakaopaycorp.moneydistribution.domain.exception;

public class AccountAlreadyPickedException extends RuntimeException {
    public AccountAlreadyPickedException() {
        super("해당 Account는 이미 받았습니다.");
    }
}
