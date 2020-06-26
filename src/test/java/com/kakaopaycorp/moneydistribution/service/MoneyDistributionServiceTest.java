package com.kakaopaycorp.moneydistribution.service;

import com.kakaopaycorp.moneydistribution.IntegrationTest;
import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.domain.MoneyPiece;
import com.kakaopaycorp.moneydistribution.domain.repository.MoneyDistributionRepository;
import com.kakaopaycorp.moneydistribution.service.exception.MoneyCanNotBeMinusException;
import com.kakaopaycorp.moneydistribution.service.exception.NotExistAccountAtChatRoomException;
import com.kakaopaycorp.moneydistribution.service.exception.PieceNumCanNotLessThanOneException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class MoneyDistributionServiceTest extends IntegrationTest {

    @Autowired
    private MoneyDistributionService moneyDistributionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MoneyDistributionRepository moneyDistributionRepository;

    @Test
    public void addMoneyDistribution() {
        //given
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        //when
        int money = 100;
        int pieceNum = 2;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        //then
        assertThat(md.getId()).isNotNull();
        assertThat(md.getMoneyPieces().stream().mapToInt(MoneyPiece::getMoneyValue).sum()).isEqualTo(money);
        assertThat(md.getMoneyPieces().size()).isEqualTo(pieceNum);
    }

    @Test(expected = MoneyCanNotBeMinusException.class)
    public void addMoneyDistribution_돈이0보다작을때() {
        //given
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        //when
        int money = -1;
        int pieceNum = 2;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        //then
        assertThat(md.getId()).isNotNull();
        assertThat(md.getMoneyPieces().stream().mapToInt(MoneyPiece::getMoneyValue).sum()).isEqualTo(money);
        assertThat(md.getMoneyPieces().size()).isEqualTo(pieceNum);
    }

    @Test(expected = PieceNumCanNotLessThanOneException.class)
    public void addMoneyDistribution_piece가1보다작을때() {
        //given
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        //when
        int money = 100;
        int pieceNum = 0;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        //then
        assertThat(md.getId()).isNotNull();
        assertThat(md.getMoneyPieces().stream().mapToInt(MoneyPiece::getMoneyValue).sum()).isEqualTo(money);
        assertThat(md.getMoneyPieces().size()).isEqualTo(pieceNum);
    }

    @Test(expected = NotExistAccountAtChatRoomException.class)
    public void addMoneyDistribution_채팅방에없는계정일때() {
        //given
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account2, account3)));

        //when
        int money = 100;
        int pieceNum = 0;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        //then
        assertThat(md.getId()).isNotNull();
        assertThat(md.getMoneyPieces().stream().mapToInt(MoneyPiece::getMoneyValue).sum()).isEqualTo(money);
        assertThat(md.getMoneyPieces().size()).isEqualTo(pieceNum);
    }

    @Test
    public void isExistTokenInAWeek() {
        MoneyDistribution md = new MoneyDistribution();
        md.setCreatedAt(LocalDateTime.now());
        md.setToken("aaa");
        this.moneyDistributionRepository.save(md);

        MoneyDistribution md2 = new MoneyDistribution();
        md2.setCreatedAt(LocalDateTime.now().minusDays(8));
        md2.setToken("bbb");
        this.moneyDistributionRepository.save(md2);

        assertThat(this.moneyDistributionService.isExistTokenInAWeek("aaa")).isTrue();
        assertThat(this.moneyDistributionService.isExistTokenInAWeek("aab")).isFalse();
        assertThat(this.moneyDistributionService.isExistTokenInAWeek("bbb")).isFalse();
    }

    @Test
    public void makeToken() {
        MoneyDistribution md = new MoneyDistribution();
        md.setCreatedAt(LocalDateTime.now());
        md.setToken("aaa");
        this.moneyDistributionRepository.save(md);

        assertThat(this.moneyDistributionService.makeToken("aaa")).isNotEqualTo("aaa");
    }
}