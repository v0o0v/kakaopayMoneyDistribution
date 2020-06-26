package com.kakaopaycorp.moneydistribution;

import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.service.AccountService;
import com.kakaopaycorp.moneydistribution.service.ChatRoomService;
import com.kakaopaycorp.moneydistribution.service.MoneyDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Profile({"!test"})
@Component
public class SampleDataGenerator implements ApplicationRunner {

    @Autowired
    AccountService accountService;

    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    MoneyDistributionService moneyDistributionService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Account account1 = this.accountService.addAccount();
        Account account2 = this.accountService.addAccount();
        Account account3 = this.accountService.addAccount();

        ChatRoom chatRoom = this.chatRoomService.makeChatRoom(new HashSet<>(Arrays.asList(account1, account2, account3)));

        MoneyDistribution moneyDistribution = this.moneyDistributionService.addMoneyDistribution(account1.getId(), chatRoom.getId(), 10000, 3);
    }
}
