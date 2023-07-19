package upbrella.be.rent.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.*;
import upbrella.be.rent.service.RentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

public class RentControllerTest extends RestDocsSupport {

    private final RentService rentService = mock(RentService.class);

    @Override
    protected Object initController() {
        return new RentController();
    }

    @DisplayName("사용자는 우산 대여 요청을 할 수 있다.")
    @Test
    void rentUmbrellaTest() throws Exception {

        RentUmbrellaByUserRequest request = RentUmbrellaByUserRequest.builder()
                .region("신촌")
                .storeId(1)
                .umbrellaId(1)
                .statusDeclaration("필요하다면 상태 신고를 해주세요.")
                .build();

        mockMvc.perform(
                        post("/rent")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("rent-umbrella-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("region").type(JsonFieldType.STRING)
                                        .description("지역"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("statusDeclaration").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("상태 신고"))
                        ));
    }

    @DisplayName("사용자는 우산 반납 요청을 할 수 있다.")
    @Test
    void returnUmbrellaTest() throws Exception {
        ReturnUmbrellaByUserRequest request = ReturnUmbrellaByUserRequest.builder()
                .umbrellaId(1)
                .storeId(1)
                .improvement("불편하셨다면 개선 사항을 입력해주세요.")
                .build();


        mockMvc.perform(
                        patch("/rent")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("return-umbrella-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("improvement").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("개선 사항"))
                        ));
    }

    @DisplayName("사용자는 우산 대여 내역을 조회 할 수 있다.")
    @Test
    void showAllRentalHistoriesTest() throws Exception {

        RentalHistoriesPageResponse response = RentalHistoriesPageResponse.builder()
                .rentalHistoryResponsePage(List.of(RentalHistoryResponse.builder()
                        .id(1L)
                        .name("사용자")
                        .phoneNumber("010-1234-5678")
                        .rentStoreName("대여점 이름")
                        .rentAt(LocalDateTime.of(2023, 7, 18, 0, 0, 0))
                        .elapsedDay(3)
                        .umbrellaId(30)
                        .returnStoreName("반납점 이름")
                        .returnAt(LocalDateTime.now())
                        .totalRentalDay(5)
                        .refundCompleted(true)
                        .etc("기타")
                        .build())
                ).build();
        System.out.println("response = " + response.getRentalHistoryResponsePage().get(0).getReturnAt());
        mockMvc.perform(
                        get("/rent/histories")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-all-rental-histories-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("rentalHistoryResponsePage").type(JsonFieldType.ARRAY)
                                        .description("대여 내역 페이지 목록"),
                                fieldWithPath("rentalHistoryResponsePage[].id").type(JsonFieldType.NUMBER)
                                        .description("대여 내역 고유번호"),
                                fieldWithPath("rentalHistoryResponsePage[].name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("rentalHistoryResponsePage[].phoneNumber").type(JsonFieldType.STRING)
                                        .description("사용자 전화번호"),
                                fieldWithPath("rentalHistoryResponsePage[].rentStoreName").type(JsonFieldType.STRING)
                                        .description("대여 지점 이름"),
                                fieldWithPath("rentalHistoryResponsePage[].rentAt")
                                        .description("대여 시간"),
                                fieldWithPath("rentalHistoryResponsePage[].elapsedDay").type(JsonFieldType.NUMBER)
                                        .description("대여 기간"),
                                fieldWithPath("rentalHistoryResponsePage[].umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("rentalHistoryResponsePage[].returnStoreName").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("반납 지점 이름"),
                                fieldWithPath("rentalHistoryResponsePage[].returnAt")
                                        .optional()
                                        .description("반납 시간"),
                                fieldWithPath("rentalHistoryResponsePage[].totalRentalDay").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("총 대여 기간"),
                                fieldWithPath("rentalHistoryResponsePage[].refundCompleted").type(JsonFieldType.BOOLEAN)
                                        .description("환불 완료 여부"),
                                fieldWithPath("rentalHistoryResponsePage[].etc").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("기타 사항")
                        )));
    }

    @DisplayName("사용자는 신고 내역을 조회할 수 있다.")
    @Test
    void showAllStatusDeclarationsTest() throws Exception {

        StatusDeclarationPageResponse response = StatusDeclarationPageResponse.builder()
                .statusDeclarationPage(List.of(StatusDeclarationResponse.builder()
                        .id(1L)
                        .umbrellaId(1)
                        .content("우산이 망가졌습니다.")
                        .etc("기타 사항")
                        .build())
                ).build();

        mockMvc.perform(
                        get("/rent/histories/status")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-all-status-declarations-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("statusDeclarationPage").type(JsonFieldType.ARRAY)
                                        .description("신고 내역"),
                                fieldWithPath("statusDeclarationPage[].id").type(JsonFieldType.NUMBER)
                                        .description("신고 내역 고유번호"),
                                fieldWithPath("statusDeclarationPage[].umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("statusDeclarationPage[].content").type(JsonFieldType.STRING)
                                        .description("신고 내용"),
                                fieldWithPath("statusDeclarationPage[].etc").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("기타 사항")
                        )));

    }

    @DisplayName("사용자는 개선 요청 내역을 조회할 수 있다.")
    @Test
    void showAllImprovementsTest() throws Exception {

        ImprovementPageResponse response = ImprovementPageResponse.builder()
                .improvementPage(List.of(ImprovementResponse.builder()
                        .id(1L)
                        .umbrellaId(1)
                        .content("정상적인 시기에 반납하기가 어려울 떈 어떻게 하죠?")
                        .etc("기타 사항")
                        .build())
                ).build();

        mockMvc.perform(
                        get("/rent/histories/improvements")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-all-improvements-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("improvementPage").type(JsonFieldType.ARRAY)
                                        .description("개선 요청 목록"),
                                fieldWithPath("improvementPage[].id").type(JsonFieldType.NUMBER)
                                        .description("개선 요청 고유번호"),
                                fieldWithPath("improvementPage[].umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("improvementPage[].content").type(JsonFieldType.STRING)
                                        .description("개선 요청 내용"),
                                fieldWithPath("improvementPage[].etc").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("기타 사항")
                        )));

    }
}