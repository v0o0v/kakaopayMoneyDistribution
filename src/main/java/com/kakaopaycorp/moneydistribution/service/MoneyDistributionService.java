package com.kakaopaycorp.moneydistribution.service;

import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.domain.repository.MoneyDistributionRepository;
import com.kakaopaycorp.moneydistribution.service.exception.MoneyCanNotBeMinusException;
import com.kakaopaycorp.moneydistribution.service.exception.NotExistAccountAtChatRoomException;
import com.kakaopaycorp.moneydistribution.service.exception.PieceNumCanNotLessThanOneException;
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
                new MoneyDistribution(chatRoom, distributor, money, pieceNum, makeToken(RandomString.make(3))));
    }


    @Transactional
    public String makeToken(String token) {
        if (this.isExistTokenInAWeek(token))
            return makeToken(RandomString.make(3));

        return token;
    }


    @Transactional
    public boolean isExistTokenInAWeek(String token) {
        return this.moneyDistributionRepository.findAllByCreatedAtAfter(LocalDateTime.now().minusDays(7))
                .stream()
                .filter(moneyDistribution -> moneyDistribution.getToken().equals(token))
                .count() >= 1;
    }


}
