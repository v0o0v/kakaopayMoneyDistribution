package com.kakaopaycorp.moneydistribution.controller;

import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.domain.MoneyPiece;
import com.kakaopaycorp.moneydistribution.service.MoneyDistributionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createMoneyDistribution(
            @RequestHeader("X-USER-ID") Long accoountId
            , @RequestHeader("X-ROOM-ID") String chatRoomId
            , @RequestBody @Valid MoneyDistributionControllerDTO.CreateRequestDTO dto
    ) {

        MoneyDistribution moneyDistribution = this.moneyDistributionService
                .addMoneyDistribution(accoountId, chatRoomId, dto.getMoney(), dto.getPieceNum());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MoneyDistributionControllerDTO.CreateResponseDTO(moneyDistribution.getToken()));

    }

    @PutMapping("/pick")
    public ResponseEntity<?> pickMoneyDistribution(
            @RequestHeader("X-USER-ID") Long accountID
            , @RequestHeader("X-ROOM-ID") String chatRoomID
            , @RequestBody @Valid MoneyDistributionControllerDTO.PickRequestDTO dto
    ) {

        MoneyPiece moneyPiece = this.moneyDistributionService
                .pickMoneyPiece(accountID, chatRoomID, dto.getToken());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MoneyDistributionControllerDTO.PickResponseDTO(moneyPiece.getMoneyValue()));

    }

    @GetMapping("/{token}")
    public ResponseEntity<?> searchMoneyDistribution(
            @RequestHeader("X-USER-ID") Long accountID
            , @RequestHeader("X-ROOM-ID") String chatRoomID
            , @PathVariable String token
    ) {

        MoneyDistribution md = this.moneyDistributionService
                .searchMoneyDistribution(accountID, chatRoomID, token);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MoneyDistributionControllerDTO.SearchResponseDTO(md));

    }
}
