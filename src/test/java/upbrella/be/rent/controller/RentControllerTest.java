package upbrella.be.rent.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.RestDocsSupport;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.RentalHistoriesPageResponse;
import upbrella.be.rent.dto.response.RentalHistoryResponse;
import upbrella.be.rent.service.RentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("region").type(JsonFieldType.STRING)
                                        .description("지역"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                                        .description("대여점 아이디"),
                                fieldWithPath("umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 아이디"),
                                fieldWithPath("statusDeclaration").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("상태 신고")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("데이터 값이 없습니다.")
                        )));
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
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 아이디"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                                        .description("대여점 아이디"),
                                fieldWithPath("improvement").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("개선 사항")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("데이터 값이 없습니다.")
                        )));
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
                        .returnAt(LocalDateTime.of(2023, 7, 23, 0, 0, 0))
                        .totalRentalDay(5)
                        .refundCompleted(true)
                        .build())
                ).build();

        mockMvc.perform(
                        get("/rent/histories")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-all-rental-histories-doc",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("데이터"),
                                fieldWithPath("data.rentalHistoryResponsePage").type(JsonFieldType.ARRAY)
                                        .description("대여 내역 페이지"),
                                fieldWithPath("data.rentalHistoryResponsePage[].id").type(JsonFieldType.NUMBER)
                                        .description("대여 내역 아이디"),
                                fieldWithPath("data.rentalHistoryResponsePage[].name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("data.rentalHistoryResponsePage[].phoneNumber").type(JsonFieldType.STRING)
                                        .description("사용자 전화번호"),
                                fieldWithPath("data.rentalHistoryResponsePage[].rentStoreName").type(JsonFieldType.STRING)
                                        .description("대여점 이름"),
                                fieldWithPath("data.rentalHistoryResponsePage[].rentAt").type(JsonFieldType.STRING)
                                        .description("대여 시간"),
                                fieldWithPath("data.rentalHistoryResponsePage[].elapsedDay").type(JsonFieldType.NUMBER)
                                        .description("대여 기간"),
                                fieldWithPath("data.rentalHistoryResponsePage[].umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 아이디"),
                                fieldWithPath("data.rentalHistoryResponsePage[].returnStoreName").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("반납점 이름"),
                                fieldWithPath("data.rentalHistoryResponsePage[].returnAt").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("반납 시간"),
                                fieldWithPath("data.rentalHistoryResponsePage[].totalRentalDay").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("총 대여 기간"),
                                fieldWithPath("data.rentalHistoryResponsePage[].refundCompleted").type(JsonFieldType.BOOLEAN)
                                        .description("환불 완료 여부")
                        )));
    }
}