package com.kakaopaycorp.moneydistribution.service;

import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.domain.repository.MoneyDistributionRepository;
import com.kakaopaycorp.moneydistribution.service.exception.MoneyCanNotBeMinusException;
import com.kakaopaycorp.moneydistribution.service.exception.NotExistAccountAtChatRoomException;
import com.kakaopaycorp.moneydistribution.service.exception.PieceNumCanNotBeMinusException;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        //TODO chatRoom에 account 있는지 확인
        ChatRoom chatRoom = this.chatRoomService.getChatRoom(chatRoomID);
        Account distributor = this.accountService.getAccount(accountID);

        if (!chatRoom.containsAccount(distributor))
            throw new NotExistAccountAtChatRoomException(distributor, chatRoom);

        if (money <= 0)
            throw new MoneyCanNotBeMinusException();

        if (pieceNum <= 0)
            throw new PieceNumCanNotBeMinusException();

        return this.moneyDistributionRepository.save(
                new MoneyDistribution(chatRoom, distributor, money, pieceNum, makeToken()));
    }

    // 최근 7일 안에 있는 같은방 뿌리기에서 토큰 중복 확인.
    // 7일이 넘은 뿌리기는 이미 의미가 없기 때문에 중복 검사 안함.
    // 토큰으로 뿌리기 조회시에도 7일안에서만 조회.
    private String makeToken() {
        return RandomString.make(3);
    }

}
