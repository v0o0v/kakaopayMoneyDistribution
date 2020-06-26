package com.kakaopaycorp.moneydistribution.service;

import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.repository.AccountRepository;
import com.kakaopaycorp.moneydistribution.service.exception.AccountEntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public Account getAccount(Long accountID) {
        return this.accountRepository.findById(accountID).orElseThrow(AccountEntityNotFoundException::new);
    }

    @Transactional
    public Account addAccount() {
        return this.accountRepository.save(new Account());
    }
}
