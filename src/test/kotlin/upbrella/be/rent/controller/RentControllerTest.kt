package upbrella.be.rent.controller

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest
import upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse
import upbrella.be.docs.utils.RestDocsSupport
import upbrella.be.rent.dto.request.HistoryFilterRequest
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest
import upbrella.be.rent.dto.response.*
import upbrella.be.rent.entity.History
import upbrella.be.rent.service.ConditionReportService
import upbrella.be.rent.service.ImprovementReportService
import upbrella.be.rent.service.LockerService
import upbrella.be.rent.service.RentService
import upbrella.be.slack.SlackAlarmService
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.user.entity.User
import upbrella.be.user.repository.UserReader
import upbrella.be.user.service.UserService
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class RentControllerTest : RestDocsSupport() {

    @Mock
    private lateinit var conditionReportService: ConditionReportService

    @Mock
    private lateinit var improvementReportService: ImprovementReportService

    @Mock
    private lateinit var rentService: RentService

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var slackAlarmService: SlackAlarmService

    @Mock
    private lateinit var lockerService: LockerService

    @Mock
    private lateinit var userReader: UserReader

    override fun initController(): Any {
        return RentController(
            conditionReportService = conditionReportService,
            improvementReportService = improvementReportService,
            rentService = rentService,
            userReader = userReader,
            slackAlarmService =slackAlarmService,
            lockerService= lockerService
        )
    }

    @Test
    @DisplayName("사용자는 대여 폼 자동 완성에 필요한 데이터를 조회할 수 있다.")
    fun findRentalFormTest() {
        // given
        val rentFormResponse = RentFormResponse(
            classificationName = "신촌",
            storeMetaId = 233L,
            rentStoreName = "motive study cafe",
            umbrellaUuid = 99L
        )

        given(rentService.findRentForm(2L))
            .willReturn(rentFormResponse)

        // when & then
        mockMvc.perform(
            get("/rent/form/{umbrellaId}", 2L)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "find-rental-form-doc",
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
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 반납 폼 자동 완성에 필요한 데이터를 조회할 수 있다.")
    fun findReturnFormTest() {
        // given
        val salt = "salt"
        val signature = "signature"

        val sessionUser = SessionUser(
            id = 1L,
            socialId = 1L,
            adminStatus = false
        )

        val userToReturn = User(
            1L,
            "테스터1",
            "010-1111-1111",
            "email",
            false,
            null,
            null,
            11L
        )

        val storeMeta = FixtureBuilderFactory.builderStoreMeta().sample()
        val umbrella = FixtureBuilderFactory.builderUmbrella().sample()

        val history = History(
            id = 1L,
            rentStoreMeta = storeMeta,
            umbrella =  umbrella,
            user = userToReturn
        )

        val session = MockHttpSession()
        session.setAttribute("user", sessionUser)

        val returnFormResponse = ReturnFormResponse.of(storeMeta, history)

        given(userReader.findUserById(1L)).willReturn(userToReturn)
        given(rentService.findReturnForm(storeMeta.id!!, userToReturn))
            .willReturn(returnFormResponse)

        // when & then
        mockMvc.perform(
            get("/return/form/{storeId}", storeMeta.id)
                .param("salt", salt)
                .param("signature", signature)
                .session(session)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "find-return-form-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("storeId")
                            .description("반납 지점 고유번호")
                    ),
                    requestParameters(
                        parameterWithName("salt").optional()
                            .description("보관함 비밀번호"),
                        parameterWithName("signature").optional()
                            .description("보관함 비밀번호 서명")
                    ),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        fieldWithPath("classificationName").type(JsonFieldType.STRING)
                            .description("지역"),
                        fieldWithPath("rentStoreName").type(JsonFieldType.STRING)
                            .description("대여 지점"),
                        fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                            .description("대여 지점 번호")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 우산 대여 요청을 할 수 있다.")
    fun rentUmbrellaTest() {
        val lockerPassword = LockerPasswordResponse("password")

        val sessionUser = SessionUser(
            id = 1L,
            socialId = 1L,
            adminStatus = false
        )

        val request = RentUmbrellaByUserRequest(
            region = "신촌",
            storeId = 1L,
            umbrellaId = 1L,
            conditionReport = "필요하다면 상태 신고를 해주세요."
        )

        val newUser = User(1L, "테스터1", "010-1111-1111", "email", false, null, null, 11L)

        val session = MockHttpSession()
        session.setAttribute("user", sessionUser)

        given(userReader.findUserById(1L)).willReturn(newUser)
        given(lockerService.findLockerPassword(any<RentUmbrellaByUserRequest>() ?: request)).willReturn(lockerPassword)

        mockMvc.perform(
            post("/rent")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "rent-umbrella-doc",
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
                            .description("상태 신고")
                    ),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                            .optional()
                            .description("보관함 비밀번호")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 우산 반납 요청을 할 수 있다.")
    fun returnUmbrellaTest() {
        val sessionUser = SessionUser(
            id = 1L,
            socialId = 1L,
            adminStatus = false
        )

        val request = ReturnUmbrellaByUserRequest(
            returnStoreId = 1L,
            bank = "우리은행",
            accountNumber = "1002-111-111111",
            improvementReportContent = "개선 요청 사항"
        )

        val session = MockHttpSession()
        session.setAttribute("user", sessionUser)

        mockMvc.perform(
            patch("/rent")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "return-umbrella-doc",
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
                            .description("개선 사항")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 우산 대여 내역을 조회 할 수 있다.")
    fun showAllRentalHistoriesTest() {
        val response = RentalHistoriesPageResponse(
            rentalHistoryResponsePage = listOf(
                RentalHistoryResponse(
                    id = 1L,
                    name = "사용자",
                    phoneNumber = "010-1234-5678",
                    rentStoreName = "대여점 이름",
                    rentAt = LocalDateTime.of(2023, 7, 18, 0, 0, 0),
                    elapsedDay = 3,
                    paid = true,
                    umbrellaUuid = 30L,
                    returnStoreName = "반납점 이름",
                    returnAt = LocalDateTime.now(),
                    totalRentalDay = 5,
                    refundCompleted = true,
                    bank = "우리은행",
                    accountNumber = "1002-111-111111",
                    etc = "기타"
                )
            ),
            countOfAllPages = 5L,
            countOfAllHistories = 22L
        )

        given(rentService.findAllHistories(any<HistoryFilterRequest>() ?: HistoryFilterRequest(false), any() ?: Pageable.unpaged())).willReturn(response)
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("refunded", "true")
        params.add("page", "0")
        params.add("size", "5")

        mockMvc.perform(
            get("/admin/rent/histories")
                .params(params)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "show-all-rental-histories-doc",
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
                        fieldWithPath("rentalHistoryResponsePage[].rentStoreName").type(
                            JsonFieldType.STRING
                        )
                            .description("대여 지점 이름"),
                        fieldWithPath("rentalHistoryResponsePage[].rentAt")
                            .description("대여 시간"),
                        fieldWithPath("rentalHistoryResponsePage[].elapsedDay").type(JsonFieldType.NUMBER)
                            .description("대여 기간"),
                        fieldWithPath("rentalHistoryResponsePage[].paid").type(JsonFieldType.BOOLEAN)
                            .description("보증금 입금 여부"),
                        fieldWithPath("rentalHistoryResponsePage[].umbrellaUuid").type(JsonFieldType.NUMBER)
                            .description("우산 고유번호"),
                        fieldWithPath("rentalHistoryResponsePage[].returnStoreName").type(
                            JsonFieldType.STRING
                        )
                            .optional()
                            .description("반납 지점 이름"),
                        fieldWithPath("rentalHistoryResponsePage[].returnAt")
                            .optional()
                            .description("반납 시간"),
                        fieldWithPath("rentalHistoryResponsePage[].totalRentalDay").type(
                            JsonFieldType.NUMBER
                        )
                            .optional()
                            .description("총 대여 기간"),
                        fieldWithPath("rentalHistoryResponsePage[].refundCompleted").type(
                            JsonFieldType.BOOLEAN
                        )
                            .description("환불 완료 여부"),
                        fieldWithPath("rentalHistoryResponsePage[].bank").type(JsonFieldType.STRING)
                            .optional()
                            .description("환불 받을 은행"),
                        fieldWithPath("rentalHistoryResponsePage[].accountNumber").type(
                            JsonFieldType.STRING
                        )
                            .optional()
                            .description("환불 받을 계좌번호"),
                        fieldWithPath("rentalHistoryResponsePage[].etc").type(JsonFieldType.STRING)
                            .optional()
                            .description("기타 사항"),
                        fieldWithPath("countOfAllPages").type(JsonFieldType.NUMBER)
                            .description("총 페이지 수"),
                        fieldWithPath("countOfAllHistories").type(JsonFieldType.NUMBER)
                            .description("총 대여 내역 수")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 신고 내역을 조회할 수 있다.")
    fun showAllStatusConditionTest() {
        val conditionReportsResponse = ConditionReportPageResponse(
            conditionReports = listOf(
                ConditionReportResponse(
                    id = 33L,
                    umbrellaUuid = 99L,
                    content = "content",
                    etc = "etc"
                )
            )
        )

        given(conditionReportService.findAll()).willReturn(conditionReportsResponse)

        mockMvc.perform(
            get("/admin/rent/histories/status")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "show-all-condition-reports-doc",
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
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 개선 요청 내역을 조회할 수 있다.")
    fun showAllImprovementsTest() {
        val improvementReportsResponse = ImprovementReportPageResponse(
            improvementReports = listOf(
                ImprovementReportResponse(
                    id = 33L,
                    umbrellaUuid = 99L,
                    content = "정상적인 시기에 반납하기가 어려울 떈 어떻게 하죠?",
                    etc = "기타 사항"
                )
            )
        )

        given(improvementReportService.findAll()).willReturn(improvementReportsResponse)

        mockMvc.perform(
            get("/admin/rent/histories/improvements")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "show-all-improvements-doc",
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
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 특정 대여 내역을 환급 처리할 수 있다.")
    fun refundRentTest() {
        // given
        val mockHttpSession = MockHttpSession()
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        mockHttpSession.setAttribute("user", sessionUser)
        doNothing().`when`(rentService).checkRefund(1L, sessionUser.id)

        // when & then
        mockMvc.perform(
            patch("/admin/rent/histories/refund/{historyId}", 1L)
                .session(mockHttpSession)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "refund-rent-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("historyId")
                            .description("대여 내역 고유번호")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 특정 대여 내역을 입금 확인 처리할 수 있다.")
    fun checkPaymentTest() {
        // given
        val mockHttpSession = MockHttpSession()
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        mockHttpSession.setAttribute("user", sessionUser)
        doNothing().`when`(rentService).checkPayment(1L, sessionUser.id)

        mockMvc.perform(
            patch("/admin/rent/histories/payment/{historyId}", 1L)
                .session(mockHttpSession)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "check-payment-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("historyId")
                            .description("대여 내역 고유번호")
                    )
                )
            )
    }

    @Test
    @DisplayName("대여 기록의 계좌 삭제 성공 테스트")
    fun deleteRentAccountTest() {
        // given
        val historyId = 1L
        doNothing().`when`(rentService).deleteBankAccount(historyId)

        // when & then
        mockMvc.perform(
            delete("/admin/rent/histories/{historyId}/account", historyId)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "delete-rent-account-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("historyId")
                            .description("대여 기록 고유번호")
                    )
                )
            )
    }
}
