package com.kakaopaycorp.moneydistribution.domain.repository;

import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

}
