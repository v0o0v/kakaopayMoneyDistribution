package com.kakaopaycorp.moneydistribution.domain.exception;

public class ValidTimeOverException extends RuntimeException {

    public ValidTimeOverException() {
        super("뿌리기 유효시간이 지났습니다.");
    }
}
