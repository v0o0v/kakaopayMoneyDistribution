package com.kakaopaycorp.moneydistribution.service.exception;

import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;

public class NotExistAccountAtChatRoomException extends RuntimeException {

    public NotExistAccountAtChatRoomException(Account account, ChatRoom chatRoom) {
        super("해당 " + account + "는 " + chatRoom + "에 존재하지 않습니다.");
    }
}
