package com.kakaopaycorp.moneydistribution.domain.repository;

import com.kakaopaycorp.moneydistribution.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
