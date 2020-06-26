package com.kakaopaycorp.moneydistribution.service;

import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.domain.MoneyPiece;
import com.kakaopaycorp.moneydistribution.domain.exception.*;
import com.kakaopaycorp.moneydistribution.domain.repository.MoneyDistributionRepository;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MoneyDistributionService {

    private ChatRoomService chatRoomService;

    private AccountService accountService;

    private MoneyDistributionRepository moneyDistributionRepository;

    public MoneyDistributionService(
            final ChatRoomService chatRoomService
            , final AccountService accountService
            , final MoneyDistributionRepository moneyDistributionRepository) {
        this.chatRoomService = chatRoomService;
        this.accountService = accountService;
        this.moneyDistributionRepository = moneyDistributionRepository;
    }

    @Transactional
    public MoneyDistribution addMoneyDistribution(long accountID, String chatRoomID, int money, int pieceNum) {
        ChatRoom chatRoom = this.chatRoomService.getChatRoom(chatRoomID);
        Account distributor = this.accountService.getAccount(accountID);

        if (!chatRoom.containsAccount(distributor))
            throw new NotExistAccountAtChatRoomException(distributor, chatRoom);

        if (money < 0)
            throw new MoneyCanNotBeMinusException();

        if (pieceNum < 1)
            throw new PieceNumCanNotLessThanOneException();

        return this.moneyDistributionRepository.save(
                new MoneyDistribution(
                        chatRoom, distributor, money, pieceNum, makeToken(RandomString.make(3), chatRoom)));
    }

    @Transactional
    public String makeToken(String token, ChatRoom chatRoom) {
        if (this.isExistTokenInAWeek(token, chatRoom))
            return makeToken(RandomString.make(3), chatRoom);

        return token;
    }

    @Transactional(readOnly = true)
    public boolean isExistTokenInAWeek(String token, ChatRoom chatRoom) {
        return this.moneyDistributionRepository
                .findAllByCreatedAtAfterAndChatRoomIs(LocalDateTime.now().minusDays(7), chatRoom)
                .stream()
                .filter(moneyDistribution -> moneyDistribution.getToken().equals(token))
                .count() >= 1;
    }

    @Transactional
    public MoneyPiece pickMoneyPiece(Long accountID, String chatRoomID, String token) {
        ChatRoom chatRoom = this.chatRoomService.getChatRoom(chatRoomID);
        Account account = this.accountService.getAccount(accountID);

        if (!chatRoom.containsAccount(account))
            throw new NotExistAccountAtChatRoomException(account, chatRoom);

        MoneyDistribution md = this.getMoneyDistribution(token, chatRoom);
        return md.pickPiece(account);
    }

    private MoneyDistribution getMoneyDistribution(String token, ChatRoom chatRoom) {
        return this.moneyDistributionRepository
                .findAllByCreatedAtAfterAndChatRoomIs(LocalDateTime.now().minusDays(7), chatRoom)
                .stream()
                .filter(moneyDistribution -> moneyDistribution.getToken().equals(token))
                .findFirst()
                .orElseThrow(NotValidTokenException::new);
    }

    @Transactional(readOnly = true)
    public MoneyDistribution searchMoneyDistribution(Long accountID, String chatRoomID, String token) {
        ChatRoom chatRoom = this.chatRoomService.getChatRoom(chatRoomID);
        Account account = this.accountService.getAccount(accountID);

        //md가 없다면 NotValidToken exception 발생
        MoneyDistribution md = this.getMoneyDistribution(token, chatRoom);

        //md의 distributor가 account가 아닐때 에러
        if(!md.getDistributor().equals(account))
            throw new MoneyDistributeAccessValidationException();

        return md;
    }
}
