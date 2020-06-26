package com.kakaopaycorp.moneydistribution.controller;

import com.kakaopaycorp.moneydistribution.ControllerTest;
import com.kakaopaycorp.moneydistribution.domain.Account;
import com.kakaopaycorp.moneydistribution.domain.ChatRoom;
import com.kakaopaycorp.moneydistribution.domain.MoneyDistribution;
import com.kakaopaycorp.moneydistribution.domain.MoneyPiece;
import com.kakaopaycorp.moneydistribution.domain.exception.*;
import com.kakaopaycorp.moneydistribution.service.MoneyDistributionService;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        md.setCreatedAt(LocalDateTime.now());
        md.setTotalDistributedMoneyValue(100);

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
                .willThrow(new NotExistAccountAtChatRoomException(account, chatRoom));

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

    @Test
    public void pickMoneyDistribution() throws Exception {
        //given
        MoneyDistributionControllerDTO.PickRequestDTO dto
                = new MoneyDistributionControllerDTO.PickRequestDTO("abc");

        Account picker = new Account();
        picker.setId(30L);
        MoneyPiece moneyPiece = makeMoneyPiece(picker);

        given(this.moneyDistributionService.pickMoneyPiece(anyLong(), anyString(), anyString()))
                .willReturn(moneyPiece);


        //when
        ResultActions result = mockMvc.perform(put("/moneyDistribution/pick")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.pickedMoneyValue").isNotEmpty())
        ;
    }

    private MoneyPiece makeMoneyPiece(Account picker) {
        MoneyPiece moneyPiece = new MoneyPiece();
        moneyPiece.setDistributeAt(LocalDateTime.now());
        moneyPiece.setPicker(picker);
        moneyPiece.setHasPicked(true);
        moneyPiece.setId(20L);
        moneyPiece.setMoneyDistribution(this.makeMoneyDistribution(10L));
        moneyPiece.setMoneyValue(55);

        return moneyPiece;
    }

    @Test
    public void pickMoneyDistribution_토큰값이없을때() throws Exception {
        //given
        MoneyDistributionControllerDTO.PickRequestDTO dto
                = new MoneyDistributionControllerDTO.PickRequestDTO("");

        Account picker = new Account();
        picker.setId(30L);
        MoneyPiece moneyPiece = makeMoneyPiece(picker);

        given(this.moneyDistributionService.pickMoneyPiece(anyLong(), anyString(), anyString()))
                .willReturn(moneyPiece);


        //when
        ResultActions result = mockMvc.perform(put("/moneyDistribution/pick")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage").value("Token 값이 비어있습니다."))
        ;
    }

    @Test
    public void pickMoneyDistribution_채팅방에없는ID로요청() throws Exception {
        //given
        MoneyDistributionControllerDTO.PickRequestDTO dto
                = new MoneyDistributionControllerDTO.PickRequestDTO("abc");

        Account picker = new Account();
        picker.setId(30L);
        MoneyPiece moneyPiece = makeMoneyPiece(picker);

        given(this.moneyDistributionService.pickMoneyPiece(anyLong(), anyString(), anyString()))
                .willThrow(new NotExistAccountAtChatRoomException(picker, moneyPiece.getMoneyDistribution().getChatRoom()));


        //when
        ResultActions result = mockMvc.perform(put("/moneyDistribution/pick")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage")
                        .value("해당 " + picker + "는 "
                                + moneyPiece.getMoneyDistribution().getChatRoom() + "에 존재하지 않습니다."))
        ;
    }

    @Test
    public void pickMoneyDistribution_10분넘은뿌리기에요청시() throws Exception {
        //given
        MoneyDistributionControllerDTO.PickRequestDTO dto
                = new MoneyDistributionControllerDTO.PickRequestDTO("abc");

        Account picker = new Account();
        picker.setId(30L);
        MoneyPiece moneyPiece = makeMoneyPiece(picker);

        given(this.moneyDistributionService.pickMoneyPiece(anyLong(), anyString(), anyString()))
                .willThrow(new ValidTimeOverException());


        //when
        ResultActions result = mockMvc.perform(put("/moneyDistribution/pick")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage")
                        .value("뿌리기 유효시간이 지났습니다."))
        ;
    }

    @Test
    public void pickMoneyDistribution_이미받아간유저가다시요청시() throws Exception {
        //given
        MoneyDistributionControllerDTO.PickRequestDTO dto
                = new MoneyDistributionControllerDTO.PickRequestDTO("abc");

        Account picker = new Account();
        picker.setId(30L);
        MoneyPiece moneyPiece = makeMoneyPiece(picker);

        given(this.moneyDistributionService.pickMoneyPiece(anyLong(), anyString(), anyString()))
                .willThrow(new AccountAlreadyPickedException());


        //when
        ResultActions result = mockMvc.perform(put("/moneyDistribution/pick")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage")
                        .value("해당 Account는 이미 받았습니다."))
        ;
    }

    @Test
    public void pickMoneyDistribution_남은뿌리기piece가없을때요청시() throws Exception {
        //given
        MoneyDistributionControllerDTO.PickRequestDTO dto
                = new MoneyDistributionControllerDTO.PickRequestDTO("abc");

        Account picker = new Account();
        picker.setId(30L);
        MoneyPiece moneyPiece = makeMoneyPiece(picker);

        given(this.moneyDistributionService.pickMoneyPiece(anyLong(), anyString(), anyString()))
                .willThrow(new UnusedPieceNoneExistException());


        //when
        ResultActions result = mockMvc.perform(put("/moneyDistribution/pick")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage")
                        .value("해당 뿌리기는 소진되었습니다."))
        ;
    }

    @Test
    public void pickMoneyDistribution_뿌리기한사람이pick요청할때() throws Exception {
        //given
        MoneyDistributionControllerDTO.PickRequestDTO dto
                = new MoneyDistributionControllerDTO.PickRequestDTO("abc");

        Account picker = new Account();
        picker.setId(30L);
        MoneyPiece moneyPiece = makeMoneyPiece(picker);

        given(this.moneyDistributionService.pickMoneyPiece(anyLong(), anyString(), anyString()))
                .willThrow(new DistributorCanNotPickException());


        //when
        ResultActions result = mockMvc.perform(put("/moneyDistribution/pick")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage")
                        .value("자신이 뿌린 것은 받을 수 없습니다"))
        ;
    }

    @Test
    public void searchMoneyDistribution() throws Exception {
        //given
        MoneyDistribution md = makeMoneyDistribution(10L);
        MoneyPiece mp1 = md.getMoneyPieces().get(0);
        mp1.setHasPicked(true);
        Account account = new Account();
        account.setId(20L);
        mp1.setPicker(account);

        given(this.moneyDistributionService.searchMoneyDistribution(anyLong(), anyString(), anyString()))
                .willReturn(md);

        //when
        ResultActions result = mockMvc.perform(get("/moneyDistribution/abc")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
        );

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.distributedAt").isNotEmpty())
                .andExpect(jsonPath("$.distributedTotalMoneyValue").isNotEmpty())
                .andExpect(jsonPath("$.pickedMoneyValue").isNotEmpty())
                .andExpect(jsonPath("$.pickedMoneyPieces").isNotEmpty())

        ;
    }

    @Test
    public void searchMoneyDistribution_요청형식이잘못된경우() throws Exception {
        //given
        MoneyDistribution md = makeMoneyDistribution(10L);
        MoneyPiece mp1 = md.getMoneyPieces().get(0);
        mp1.setHasPicked(true);
        Account account = new Account();
        account.setId(20L);
        mp1.setPicker(account);

        given(this.moneyDistributionService.searchMoneyDistribution(anyLong(), anyString(), anyString()))
                .willReturn(md);

        //when
        ResultActions result = mockMvc.perform(get("/moneyDistribution/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
        );

        //then
        result
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        ;
    }

    @Test
    public void searchMoneyDistribution_조회할수없는토큰() throws Exception {
        //given
        MoneyDistribution md = makeMoneyDistribution(10L);
        MoneyPiece mp1 = md.getMoneyPieces().get(0);
        mp1.setHasPicked(true);
        Account account = new Account();
        account.setId(20L);
        mp1.setPicker(account);

        given(this.moneyDistributionService.searchMoneyDistribution(anyLong(), anyString(), anyString()))
                .willThrow(new NotValidTokenException());

        //when
        ResultActions result = mockMvc.perform(get("/moneyDistribution/abc")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
        );

        //then
        result
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage")
                        .value("Token이 유효하지 않습니다."))
        ;
    }

    @Test
    public void searchMoneyDistribution_뿌리기한사람이조회하지않은경우() throws Exception {
        //given
        MoneyDistribution md = makeMoneyDistribution(10L);
        MoneyPiece mp1 = md.getMoneyPieces().get(0);
        mp1.setHasPicked(true);
        Account account = new Account();
        account.setId(20L);
        mp1.setPicker(account);

        given(this.moneyDistributionService.searchMoneyDistribution(anyLong(), anyString(), anyString()))
                .willThrow(new MoneyDistributeAccessValidationException());

        //when
        ResultActions result = mockMvc.perform(get("/moneyDistribution/abc")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", 1L)
                .header("X-ROOM-ID", 2L)
        );

        //then
        result
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errorMessage")
                        .value("자신의 뿌리기만 접근 가능합니다."))
        ;
    }
}