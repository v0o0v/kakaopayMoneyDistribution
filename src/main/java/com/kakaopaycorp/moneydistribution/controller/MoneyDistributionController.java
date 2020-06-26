package com.kakaopaycorp.moneydistribution.controller;

import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.service.MoneyDistributionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/moneyDistribution")
public class MoneyDistributionController {

    private MoneyDistributionService moneyDistributionService;

    public MoneyDistributionController(final MoneyDistributionService moneyDistributionService) {
        this.moneyDistributionService = moneyDistributionService;
    }

    @PostMapping
    public ResponseEntity<MoneyDistribution> createMoneyDistribution(
            @RequestHeader("X-USER-ID") Long accoountId
            , @RequestHeader("X-ROOM-ID") String chatRoomId
            , @RequestBody @Valid MoneyDistributionControllerDTO.MoneyDistributionCreateDTO dto) {

        MoneyDistribution moneyDistribution = this.moneyDistributionService
                .addMoneyDistribution(accoountId, chatRoomId, dto.getMoney(), dto.getPieceNum());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(moneyDistribution);
    }
}
