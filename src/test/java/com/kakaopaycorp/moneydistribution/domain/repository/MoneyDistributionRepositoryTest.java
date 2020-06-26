package com.kakaopaycorp.moneydistribution.domain.repository;

import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MoneyDistributionRepositoryTest {

    @Autowired
    MoneyDistributionRepository moneyDistributionRepository;

    @Test
    public void findAllByCreatedAtAfter() {
        LocalDateTime now = LocalDateTime.now();
        MoneyDistribution md1 = new MoneyDistribution();
        md1.setCreatedAt(now.minusDays(6).minusHours(23).minusMinutes(59).minusSeconds(59));
        md1 = moneyDistributionRepository.save(md1);

        MoneyDistribution md2 = new MoneyDistribution();
        md2.setCreatedAt(now.minusDays(7));
        md2 = moneyDistributionRepository.save(md2);

        List<MoneyDistribution> sevenDay = moneyDistributionRepository.findAllByCreatedAtAfter(now.minusDays(7));

        assertThat(sevenDay.size()).isEqualTo(1);
        assertThat(sevenDay.get(0)).isEqualTo(md1);
    }
}