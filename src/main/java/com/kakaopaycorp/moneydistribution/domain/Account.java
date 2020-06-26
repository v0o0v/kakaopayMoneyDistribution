package com.kakaopaycorp.moneydistribution.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany(mappedBy = "chatters")
    private Set<ChatRoom> chatRoom;

    public Account() {
        this.chatRoom = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                '}';
    }

    public void addChatRoom(ChatRoom chatRoom){
        this.chatRoom.add(chatRoom);
    }
}
