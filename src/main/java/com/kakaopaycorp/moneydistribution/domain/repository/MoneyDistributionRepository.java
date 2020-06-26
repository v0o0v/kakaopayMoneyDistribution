package com.kakaopaycorp.moneydistribution.domain.repository;

import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MoneyDistributionRepository extends JpaRepository<MoneyDistribution, Long> {

    List<MoneyDistribution> findAllByCreatedAtAfter(LocalDateTime ldt);
}
