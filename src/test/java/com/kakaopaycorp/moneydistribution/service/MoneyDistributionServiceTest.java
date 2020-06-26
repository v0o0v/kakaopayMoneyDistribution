package com.kakaopaycorp.moneydistribution.service;

import com.kakaopaycorp.moneydistribution.IntegrationTest;
import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.domain.MoneyPiece;
import com.kakaopaycorp.moneydistribution.domain.exception.*;
import com.kakaopaycorp.moneydistribution.domain.repository.ChatRoomRepository;
import com.kakaopaycorp.moneydistribution.domain.repository.MoneyDistributionRepository;
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

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    public void addMoneyDistribution() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 2;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        assertThat(md.getId()).isNotNull();
        assertThat(md.getMoneyPieces().stream().mapToInt(MoneyPiece::getMoneyValue).sum()).isEqualTo(money);
        assertThat(md.getMoneyPieces().size()).isEqualTo(pieceNum);
    }

    @Test(expected = MoneyCanNotBeMinusException.class)
    public void addMoneyDistribution_돈이0보다작을때() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = -1;
        int pieceNum = 2;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        assertThat(md.getId()).isNotNull();
        assertThat(md.getMoneyPieces().stream().mapToInt(MoneyPiece::getMoneyValue).sum()).isEqualTo(money);
        assertThat(md.getMoneyPieces().size()).isEqualTo(pieceNum);
    }

    @Test(expected = PieceNumCanNotLessThanOneException.class)
    public void addMoneyDistribution_piece가1보다작을때() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 0;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        assertThat(md.getId()).isNotNull();
        assertThat(md.getMoneyPieces().stream().mapToInt(MoneyPiece::getMoneyValue).sum()).isEqualTo(money);
        assertThat(md.getMoneyPieces().size()).isEqualTo(pieceNum);
    }

    @Test(expected = NotExistAccountAtChatRoomException.class)
    public void addMoneyDistribution_채팅방에없는계정일때() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account2, account3)));

        int money = 100;
        int pieceNum = 0;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        assertThat(md.getId()).isNotNull();
        assertThat(md.getMoneyPieces().stream().mapToInt(MoneyPiece::getMoneyValue).sum()).isEqualTo(money);
        assertThat(md.getMoneyPieces().size()).isEqualTo(pieceNum);
    }

    @Test
    public void isExistTokenInAWeek() {
        ChatRoom chatRoom = this.chatRoomRepository.save(new ChatRoom());

        MoneyDistribution md = new MoneyDistribution();
        md.setCreatedAt(LocalDateTime.now());
        md.setToken("aaa");
        md.setChatRoom(chatRoom);
        this.moneyDistributionRepository.save(md);

        MoneyDistribution md2 = new MoneyDistribution();
        md2.setCreatedAt(LocalDateTime.now().minusDays(8));
        md2.setToken("bbb");
        md2.setChatRoom(chatRoom);
        this.moneyDistributionRepository.save(md2);

        assertThat(this.moneyDistributionService.isExistTokenInAWeek("aaa", chatRoom)).isTrue();
        assertThat(this.moneyDistributionService.isExistTokenInAWeek("aab", chatRoom)).isFalse();
        assertThat(this.moneyDistributionService.isExistTokenInAWeek("bbb", chatRoom)).isFalse();
    }

    @Test
    public void makeToken() {
        ChatRoom chatRoom = this.chatRoomRepository.save(new ChatRoom());

        MoneyDistribution md = new MoneyDistribution();
        md.setCreatedAt(LocalDateTime.now());
        md.setToken("aaa");
        md.setChatRoom(chatRoom);
        this.moneyDistributionRepository.save(md);

        assertThat(this.moneyDistributionService.makeToken("aaa", chatRoom)).isNotEqualTo("aaa");
    }

    @Test
    public void pickMoneyPiece() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 3;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        MoneyPiece moneyPiece = md.pickPiece(account2);
        assertThat(moneyPiece.getPicker()).isEqualTo(account2);
        assertThat(moneyPiece.isHasPicked()).isTrue();
        assertThat(md.getMoneyPieces()
                .stream()
                .filter(moneyPiece1 -> !moneyPiece1.isHasPicked())
                .count())
                .isEqualTo(2);
    }

    @Test(expected = NotExistAccountAtChatRoomException.class)
    public void pickMoneyPiece_채팅방에없는ID로요청() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2)));

        int money = 100;
        int pieceNum = 3;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account3.getId(), chatRoom.getId(), money, pieceNum);

        MoneyPiece moneyPiece = md.pickPiece(account2);
        assertThat(moneyPiece.getPicker()).isEqualTo(account2);
        assertThat(moneyPiece.isHasPicked()).isTrue();
        assertThat(md.getMoneyPieces()
                .stream()
                .filter(moneyPiece1 -> !moneyPiece1.isHasPicked())
                .count())
                .isEqualTo(2);
    }

    @Test(expected = ValidTimeOverException.class)
    public void pickMoneyPiece_10분넘은뿌리기에요청시() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 3;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);
        md.setCreatedAt(LocalDateTime.now().minusMinutes(11));
        this.moneyDistributionRepository.save(md);

        MoneyPiece moneyPiece = md.pickPiece(account2);
        assertThat(moneyPiece.getPicker()).isEqualTo(account2);
        assertThat(moneyPiece.isHasPicked()).isTrue();
        assertThat(md.getMoneyPieces()
                .stream()
                .filter(moneyPiece1 -> !moneyPiece1.isHasPicked())
                .count())
                .isEqualTo(2);
    }

    @Test(expected = AccountAlreadyPickedException.class)
    public void pickMoneyPiece_이미받아간유저가다시요청시() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 3;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        MoneyPiece moneyPiece = md.pickPiece(account2);
        moneyPiece = md.pickPiece(account2);

        assertThat(moneyPiece.getPicker()).isEqualTo(account2);
        assertThat(moneyPiece.isHasPicked()).isTrue();
        assertThat(md.getMoneyPieces()
                .stream()
                .filter(moneyPiece1 -> !moneyPiece1.isHasPicked())
                .count())
                .isEqualTo(2);
    }

    @Test(expected = UnusedPieceNoneExistException.class)
    public void pickMoneyPiece_남은뿌리기piece가없을때요청시() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 1;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        MoneyPiece moneyPiece = md.pickPiece(account2);
        moneyPiece = md.pickPiece(account3);


        assertThat(moneyPiece.getPicker()).isEqualTo(account2);
        assertThat(moneyPiece.isHasPicked()).isTrue();
        assertThat(md.getMoneyPieces()
                .stream()
                .filter(moneyPiece1 -> !moneyPiece1.isHasPicked())
                .count())
                .isEqualTo(2);
    }

    @Test(expected = DistributorCanNotPickException.class)
    public void pickMoneyPiece_뿌리기한사람이pick요청할때() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 3;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        MoneyPiece moneyPiece = md.pickPiece(account1);

        assertThat(moneyPiece.getPicker()).isEqualTo(account2);
        assertThat(moneyPiece.isHasPicked()).isTrue();
        assertThat(md.getMoneyPieces()
                .stream()
                .filter(moneyPiece1 -> !moneyPiece1.isHasPicked())
                .count())
                .isEqualTo(2);
    }

    @Test
    public void searchMoneyDistribution() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 3;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        MoneyDistribution serarchMD = this.moneyDistributionService
                .searchMoneyDistribution(account1.getId(), md.getChatRoom().getId(), md.getToken());

        assertThat(md).isEqualTo(serarchMD);
    }

    @Test(expected = NotValidTokenException.class)
    public void searchMoneyDistribution_잘못된토큰으로검색() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 3;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        MoneyDistribution serarchMD = this.moneyDistributionService
                .searchMoneyDistribution(account1.getId(), md.getChatRoom().getId(), "111");

        assertThat(md).isEqualTo(serarchMD);
    }

    @Test(expected = ChatRoomEntityNotFoundException.class)
    public void searchMoneyDistribution_다른채팅룸으로검색시() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 3;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        MoneyDistribution serarchMD = this.moneyDistributionService
                .searchMoneyDistribution(account1.getId(), "sdfafd", md.getToken());

        assertThat(md).isEqualTo(serarchMD);
    }

    @Test(expected = MoneyDistributeAccessValidationException.class)
    public void searchMoneyDistribution_뿌리기당사자가아닌account로검색시() {
        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();
        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        int money = 100;
        int pieceNum = 3;
        MoneyDistribution md = this.moneyDistributionService
                .addMoneyDistribution(account1.getId(), chatRoom.getId(), money, pieceNum);

        MoneyDistribution serarchMD = this.moneyDistributionService
                .searchMoneyDistribution(account2.getId(), chatRoom.getId(), md.getToken());

        assertThat(md).isEqualTo(serarchMD);
    }
}