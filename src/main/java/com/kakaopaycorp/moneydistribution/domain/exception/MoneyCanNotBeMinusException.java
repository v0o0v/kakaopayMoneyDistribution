package com.kakaopaycorp.moneydistribution.domain.exception;

public class MoneyCanNotBeMinusException extends RuntimeException {

    public MoneyCanNotBeMinusException() {
        super("뿌리기 지정 액수는 0보디 작을 수 없습니다..");
    }
}
