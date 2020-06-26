package com.kakaopaycorp.moneydistribution.domain.exception;

public class UnusedPieceNoneExistException extends RuntimeException {

    public UnusedPieceNoneExistException() {
        super("해당 뿌리기는 소진되었습니다.");
    }
}
