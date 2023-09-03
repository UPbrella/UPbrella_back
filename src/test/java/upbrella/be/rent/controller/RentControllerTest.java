package upbrella.be.rent.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.*;
import upbrella.be.rent.service.ConditionReportService;
import upbrella.be.rent.service.ImprovementReportService;
import upbrella.be.rent.service.RentService;
import upbrella.be.slack.service.SlackAlarmService;
import upbrella.be.user.dto.response.SessionUser;
import upbrella.be.user.entity.User;
import upbrella.be.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

@ExtendWith(MockitoExtension.class)
public class RentControllerTest extends RestDocsSupport {

    @Mock
    private ConditionReportService conditionReportService;
    @Mock
    private ImprovementReportService improvementReportService;
    @Mock
    private RentService rentService;
    @Mock
    private UserService userService;

    @Mock
    private SlackAlarmService slackAlarmService;

    @Override
    protected Object initController() {
        return new RentController(conditionReportService, improvementReportService, rentService, userService, slackAlarmService);
    }

    @Test
    @DisplayName("사용자는 대여 폼 자동 완성에 필요한 데이터를 조회할 수 있다.")
    void findRentalFormTest() throws Exception {

        // given
        RentFormResponse rentFormResponse = RentFormResponse.builder()
                .classificationName("신촌")
                .storeMetaId(233L)
                .rentStoreName("motive study cafe")
                .umbrellaUuid(99L)
                .build();

        given(rentService.findRentForm(2L))
                .willReturn(rentFormResponse);

        // when & then
        mockMvc.perform(
                        get("/rent/form/{umbrellaId}", 2L)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find-rental-form-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("umbrellaId")
                                        .description("우산 번호 (uuid 아님)")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("classificationName").type(JsonFieldType.STRING)
                                        .description("지역"),
                                fieldWithPath("storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("rentStoreName").type(JsonFieldType.STRING)
                                        .description("대여 지점 이름"),
                                fieldWithPath("umbrellaUuid").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호")
                        )));
    }

    @Test
    @DisplayName("사용자는 우산 대여 요청을 할 수 있다.")
    void rentUmbrellaTest() throws Exception {

        SessionUser sessionUser = SessionUser.builder()
                .id(1L)
                .socialId(1L)
                .adminStatus(false)
                .build();

        RentUmbrellaByUserRequest request = RentUmbrellaByUserRequest.builder()
                .region("신촌")
                .storeId(1L)
                .umbrellaId(1L)
                .conditionReport("필요하다면 상태 신고를 해주세요.")
                .build();

        User newUser = User.builder()
                .id(1L)
                .name("테스터1")
                .phoneNumber("010-1111-1111")
                .adminStatus(false)
                .build();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", sessionUser);


        given(userService.findUserById(1L)).willReturn(newUser);

        mockMvc.perform(
                        post("/rent")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session)
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
                                fieldWithPath("conditionReport").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("상태 신고"))
                ));
    }

    @Test
    @DisplayName("사용자는 우산 반납 요청을 할 수 있다.")
    void returnUmbrellaTest() throws Exception {

        SessionUser sessionUser = SessionUser.builder()
                .id(1L)
                .socialId(1L)
                .adminStatus(false)
                .build();

        ReturnUmbrellaByUserRequest request = ReturnUmbrellaByUserRequest.builder()
                .returnStoreId(1L)
                .bank("우리은행")
                .accountNumber("1002-111-111111")
                .improvementReportContent("개선 요청 사항")
                .build();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", sessionUser);


        mockMvc.perform(
                        patch("/rent")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("return-umbrella-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("returnStoreId").type(JsonFieldType.NUMBER)
                                        .description("반납 지점 고유번호"),
                                fieldWithPath("bank").type(JsonFieldType.STRING)
                                        .description("환급 받을 은행"),
                                fieldWithPath("accountNumber").type(JsonFieldType.STRING)
                                        .description("환급 받을 계좌번호"),
                                fieldWithPath("improvementReportContent").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("개선 사항"))
                ));
    }

    @Test
    @DisplayName("사용자는 우산 대여 내역을 조회 할 수 있다.")
    void showAllRentalHistoriesTest() throws Exception {

        RentalHistoriesPageResponse response = RentalHistoriesPageResponse.builder()
                .rentalHistoryResponsePage(List.of(RentalHistoryResponse.builder()
                        .id(1L)
                        .name("사용자")
                        .phoneNumber("010-1234-5678")
                        .rentStoreName("대여점 이름")
                        .rentAt(LocalDateTime.of(2023, 7, 18, 0, 0, 0))
                        .elapsedDay(3)
                        .paid(true)
                        .umbrellaUuid(30L)
                        .returnStoreName("반납점 이름")
                        .returnAt(LocalDateTime.now())
                        .totalRentalDay(5)
                        .refundCompleted(true)
                        .bank("우리은행")
                        .accountNumber("1002-111-111111")
                        .etc("기타")
                        .build())
                )
                .countOfAllPages(5L)
                .countOfAllHistories(22L)
                .build();

        given(rentService.findAllHistories(any(), any())).willReturn(response);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("refunded", "true");
        params.add("page", "0");
        params.add("size", "5");

        mockMvc.perform(
                        get("/rent/histories")
                                .params(params)
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
                                fieldWithPath("rentalHistoryResponsePage[].paid").type(JsonFieldType.BOOLEAN)
                                        .description("보증금 입금 여부"),
                                fieldWithPath("rentalHistoryResponsePage[].umbrellaUuid").type(JsonFieldType.NUMBER)
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
                                fieldWithPath("rentalHistoryResponsePage[].bank").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("환불 받을 은행"),
                                fieldWithPath("rentalHistoryResponsePage[].accountNumber").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("환불 받을 계좌번호"),
                                fieldWithPath("rentalHistoryResponsePage[].etc").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("기타 사항"),
                                fieldWithPath("countOfAllPages").type(JsonFieldType.NUMBER)
                                        .description("총 페이지 수"),
                                fieldWithPath("countOfAllHistories").type(JsonFieldType.NUMBER)
                                        .description("총 대여 내역 수")
                        )));
    }

    @Test
    @DisplayName("사용자는 신고 내역을 조회할 수 있다.")
    void showAllStatusConditionTest() throws Exception {

        ConditionReportPageResponse conditionReportsResponse = ConditionReportPageResponse.builder()
                .conditionReports(
                        List.of(
                                ConditionReportResponse.builder()
                                        .id(33L)
                                        .umbrellaUuid(99L)
                                        .content("content")
                                        .etc("etc")
                                        .build())
                ).build();

        given(conditionReportService.findAll()).willReturn(conditionReportsResponse);

        mockMvc.perform(
                        get("/rent/histories/status")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-all-condition-reports-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("conditionReports").type(JsonFieldType.ARRAY)
                                        .description("신고 내역 페이지"),
                                fieldWithPath("conditionReports[].id").type(JsonFieldType.NUMBER)
                                        .description("신고 내역 고유번호"),
                                fieldWithPath("conditionReports[].umbrellaUuid").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("conditionReports[].content").type(JsonFieldType.STRING)
                                        .description("신고 내용"),
                                fieldWithPath("conditionReports[].etc").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("기타 사항")
                        )));

    }

    @Test
    @DisplayName("사용자는 개선 요청 내역을 조회할 수 있다.")
    void showAllImprovementsTest() throws Exception {

        ImprovementReportPageResponse improvementReportsResponse = ImprovementReportPageResponse.builder()
                .improvementReports(List.of(ImprovementReportResponse.builder()
                        .id(33L)
                        .umbrellaUuid(99L)
                        .content("정상적인 시기에 반납하기가 어려울 떈 어떻게 하죠?")
                        .etc("기타 사항")
                        .build())
                ).build();

        given(improvementReportService.findAll()).willReturn(improvementReportsResponse);

        mockMvc.perform(
                        get("/rent/histories/improvements")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-all-improvements-doc",
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("improvementReports").type(JsonFieldType.ARRAY)
                                        .description("개선 요청 목록"),
                                fieldWithPath("improvementReports[].id").type(JsonFieldType.NUMBER)
                                        .description("개선 요청 고유번호"),
                                fieldWithPath("improvementReports[].umbrellaUuid").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("improvementReports[].content").type(JsonFieldType.STRING)
                                        .description("개선 요청 내용"),
                                fieldWithPath("improvementReports[].etc").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("기타 사항")
                        )));

    }

    @Test
    @DisplayName("사용자는 특정 대여 내역을 환급 처리할 수 있다.")
    void refundRentTest() throws Exception {

        // given
        MockHttpSession mockHttpSession = new MockHttpSession();
        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().sample();
        mockHttpSession.setAttribute("user", sessionUser);
        doNothing().when(rentService)
                .checkRefund(1L, sessionUser.getId());

        mockMvc.perform(
                        patch("/rent/histories/refund/{historyId}", 1L)
                                .session(mockHttpSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("refund-rent-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("historyId")
                                        .description("대여 내역 고유번호")
                        )));

    }

    @Test
    @DisplayName("사용자는 특정 대여 내역을 입금 확인 처리할 수 있다.")
    void checkPaymentTest() throws Exception {

        // given
        MockHttpSession mockHttpSession = new MockHttpSession();
        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().sample();
        mockHttpSession.setAttribute("user", sessionUser);
        doNothing().when(rentService)
                .checkPayment(1L, sessionUser.getId());

        mockMvc.perform(
                        patch("/rent/histories/payment/{historyId}", 1L)
                                .session(mockHttpSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("check-payment-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("historyId")
                                        .description("대여 내역 고유번호")
                        )));

    }
}
