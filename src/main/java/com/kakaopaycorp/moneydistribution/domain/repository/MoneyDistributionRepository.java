package com.kakaopaycorp.moneydistribution.domain.repository;

import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyDistributionRepository extends JpaRepository<MoneyDistribution, Long> {

}
