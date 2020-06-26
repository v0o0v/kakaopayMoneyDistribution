package com.kakaopaycorp.moneydistribution.domain.exception;

public class DistributorCanNotPickException extends RuntimeException {

    public DistributorCanNotPickException() {
        super("자신이 뿌린 것은 받을 수 없습니다");
    }
}
