package com.kakaopaycorp.moneydistribution.service.exception;

public class PieceNumCanNotLessThanOneException extends RuntimeException {

    public PieceNumCanNotLessThanOneException() {
        super("뿌리기 지정 대상 인원은 1보다 작을 수 없습니다.");
    }
}
