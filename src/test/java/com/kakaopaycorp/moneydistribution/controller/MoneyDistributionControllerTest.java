package com.kakaopaycorp.moneydistribution.controller;

import com.kakaopaycorp.moneydistribution.ControllerTest;
import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.domain.MoneyPiece;
import com.kakaopaycorp.moneydistribution.service.MoneyDistributionService;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MoneyDistributionController.class)
public class MoneyDistributionControllerTest extends ControllerTest {

    @MockBean
    MoneyDistributionService moneyDistributionService;

    @Test
    public void createMoneyDistribution() throws Exception {

        //given
        MoneyDistributionControllerDTO.MoneyDistributionCreateDTO dto
                = new MoneyDistributionControllerDTO.MoneyDistributionCreateDTO(100, 3);

        MoneyDistribution md = makeMoneyDistribution(1L);
        given(this.moneyDistributionService.addMoneyDistribution(anyLong(), any(), anyInt(), anyInt())).willReturn(md);

        //when
        ResultActions result = mockMvc.perform(post("/moneyDistribution")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(md.getId()))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.chatRoom").isNotEmpty())
                .andExpect(jsonPath("$.distributor").isNotEmpty())
        ;
    }

    private MoneyDistribution makeMoneyDistribution(long mdID) {

        MoneyDistribution md = new MoneyDistribution();

        md.setId(mdID);
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId("chatRoomID");
        md.setChatRoom(chatRoom);
        Account distributor = new Account();
        distributor.setId(2L);
        md.setDistributor(distributor);
        md.setToken("abc");
        MoneyPiece mp1 = new MoneyPiece(md,33);
        MoneyPiece mp2 = new MoneyPiece(md, 33);
        MoneyPiece mp3 = new MoneyPiece(md, 34);
        md.setMoneyPieces(Arrays.asList(mp1, mp2, mp3));

        return md;
    }
}