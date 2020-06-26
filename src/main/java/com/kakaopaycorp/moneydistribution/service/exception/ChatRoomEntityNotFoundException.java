package com.kakaopaycorp.moneydistribution.service.exception;

public class ChatRoomEntityNotFoundException extends RuntimeException {
    public ChatRoomEntityNotFoundException() {
        super("ChatRoom Entity를 찾을 수 없습니다.");
    }
}
