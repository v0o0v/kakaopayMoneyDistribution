package com.kakaopaycorp.moneydistribution.service.exception;

public class PieceNumCanNotBeMinusException extends RuntimeException {

    public PieceNumCanNotBeMinusException() {
        super("분배 인원은 0보다 작을 수 없습니다.");
    }
}
