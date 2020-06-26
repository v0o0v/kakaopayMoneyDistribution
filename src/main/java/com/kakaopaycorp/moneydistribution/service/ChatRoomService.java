package com.kakaopaycorp.moneydistribution.service;

import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.repository.ChatRoomRepository;
import com.kakaopaycorp.moneydistribution.domain.exception.ChatRoomEntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
public class ChatRoomService {

    private ChatRoomRepository chatRoomRepository;

    public ChatRoomService(final ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoom(String chatRoomID) {
        return this.chatRoomRepository.findById(chatRoomID).orElseThrow(ChatRoomEntityNotFoundException::new);
    }

    @Transactional
    public ChatRoom makeChatRoom(Set<Account> chatters) {
        ChatRoom chatRoom = this.chatRoomRepository.save(new ChatRoom());
        chatRoom.addChatters(chatters);
        return chatRoom;
    }
}
