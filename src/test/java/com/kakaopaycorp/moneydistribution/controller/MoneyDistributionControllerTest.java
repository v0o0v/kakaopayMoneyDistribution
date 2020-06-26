package com.kakaopaycorp.moneydistribution.controller;

import com.kakaopaycorp.moneydistribution.ControllerTest;
import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.domain.MoneyPiece;
import com.kakaopaycorp.moneydistribution.service.MoneyDistributionService;
import com.kakaopaycorp.moneydistribution.service.exception.NotExistAccountAtChatRoomException;
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
        MoneyDistributionControllerDTO.CreateRequestDTO dto
                = new MoneyDistributionControllerDTO.CreateRequestDTO(100, 3);

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
                .andExpect(jsonPath("$.token").isNotEmpty())
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
        MoneyPiece mp1 = new MoneyPiece(md, 33);
        MoneyPiece mp2 = new MoneyPiece(md, 33);
        MoneyPiece mp3 = new MoneyPiece(md, 34);
        md.setMoneyPieces(Arrays.asList(mp1, mp2, mp3));

        return md;
    }

    @Test
    public void createMoneyDistribution_머니가0보다작을때() throws Exception {

        //given
        MoneyDistributionControllerDTO.CreateRequestDTO dto
                = new MoneyDistributionControllerDTO.CreateRequestDTO(-1, 3);

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
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage").value("뿌리기 지정 액수는 0보디 작을 수 없습니다."))
        ;
    }

    @Test
    public void createMoneyDistribution_지정인원이1보다작을때() throws Exception {

        //given
        MoneyDistributionControllerDTO.CreateRequestDTO dto
                = new MoneyDistributionControllerDTO.CreateRequestDTO(100, 0);

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
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage").value("뿌리기 지정 대상 인원은 1보다 작을 수 없습니다."))
        ;
    }

    @Test
    public void createMoneyDistribution_채팅방에없는account() throws Exception {

        //given
        MoneyDistributionControllerDTO.CreateRequestDTO dto
                = new MoneyDistributionControllerDTO.CreateRequestDTO(100, 3);

        Account account = new Account();
        account.setId(1L);
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId("aaa-bbb");

        MoneyDistribution md = makeMoneyDistribution(1L);
        given(this.moneyDistributionService
                .addMoneyDistribution(anyLong(), any(), anyInt(), anyInt()))
                .willThrow(new NotExistAccountAtChatRoomException(account,chatRoom));

        //when
        ResultActions result = mockMvc.perform(post("/moneyDistribution")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage").value("해당 Account{id=1}는 ChatRoom{id='aaa-bbb'}에 존재하지 않습니다."))
        ;
    }
}