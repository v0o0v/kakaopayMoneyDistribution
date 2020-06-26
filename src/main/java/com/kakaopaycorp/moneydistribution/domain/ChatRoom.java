package com.kakaopaycorp.moneydistribution.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class ChatRoom {

    @Id
    private String id;

    @ManyToMany(mappedBy = "chatRoom")
    private Set<Account> chatters;

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id='" + id + '\'' +
                '}';
    }

    public ChatRoom() {
        this.id = UUID.randomUUID().toString();
        this.chatters = new HashSet<>();
    }

    public void addChatters(Set<Account> chatters) {
        chatters.forEach(account -> {
            this.chatters.add(account);
            account.addChatRoom(this);
        });
    }

    public boolean containsAccount(Account distributor) {
        return this.chatters.contains(distributor);
    }
}
