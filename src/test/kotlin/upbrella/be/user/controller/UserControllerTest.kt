package upbrella.be.user.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.HttpClientErrorException
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.config.FixtureFactory
import upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest
import upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse
import upbrella.be.docs.utils.RestDocsSupport
import upbrella.be.rent.service.RentService
import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.request.KakaoAccount
import upbrella.be.user.dto.request.LoginCodeRequest
import upbrella.be.user.dto.response.*
import upbrella.be.user.dto.token.AppleOauthInfo
import upbrella.be.user.dto.token.KakaoOauthInfo
import upbrella.be.user.dto.token.OauthToken
import upbrella.be.user.entity.User
import upbrella.be.user.exception.*
import upbrella.be.user.service.BlackListService
import upbrella.be.user.service.OauthLoginService
import upbrella.be.user.service.UserService
import upbrella.be.util.AesEncryptor
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UserControllerTest (
    @Mock
    private val oauthLoginService: OauthLoginService,
    @Mock
    private val userService: UserService,
    @Mock
    private val kakaoOauthInfo: KakaoOauthInfo,
    @Mock
    private val appleOauthInfo: AppleOauthInfo,
    @Mock
    private val rentService: RentService,
    @Mock
    private val aesEncryptor: AesEncryptor,
    @Mock
    private val blackListService: BlackListService,
) : RestDocsSupport() {

    override fun initController(): Any {
        return UserController(
            oauthLoginService = oauthLoginService,
            userService = userService,
            kakaoOauthInfo = kakaoOauthInfo,
            appleOauthInfo = appleOauthInfo,
            rentService = rentService,
            blackListService = blackListService,
        )
    }

    @Test
    @DisplayName("사용자는 로그인된 유저 정보를 조회할 수 있다.")
    fun findUserInfoTest() {
        // given
        val session = MockHttpSession()
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        val user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()

        session.setAttribute("user", sessionUser)
        user.decryptData(aesEncryptor)

        given(userService.findDecryptedUserById(sessionUser))
            .willReturn(user)

        // when & then
        mockMvc.perform(
            get("/users/loggedIn")
                .session(session)
        )
            .andDo { println(it.response.contentAsString) }
            .andExpect(status().isOk)
            .andDo(
                document(
                    "find-user-info-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        fieldWithPath("id").type(JsonFieldType.NUMBER)
                            .description("사용자 고유번호"),
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("사용자 이름"),
                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                            .description("사용자 전화번호"),
                        fieldWithPath("bank").type(JsonFieldType.STRING)
                            .description("사용자 은행")
                            .optional(),
                        fieldWithPath("accountNumber").type(JsonFieldType.STRING)
                            .description("사용자 계좌번호")
                            .optional(),
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("사용자 이메일"),
                        fieldWithPath("adminStatus").type(JsonFieldType.BOOLEAN)
                            .description("관리자 여부")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 유저가 빌린 우산을 조회할 수 있다.")
    fun findUmbrellaBorrowedByUserTest() {
        // given
        val httpSession = MockHttpSession()
        val user = FixtureBuilderFactory.builderSessionUser().sample()

        httpSession.setAttribute("user", user)

        val borrowedUmbrella = FixtureBuilderFactory.builderUmbrella().sample()
        val history = FixtureBuilderFactory.builderHistory(aesEncryptor).sample()
        val elapsedDay = LocalDateTime.now().dayOfYear - history.rentedAt.dayOfYear

        given(userService.findUmbrellaBorrowedByUser(user))
            .willReturn(UmbrellaBorrowedByUserResponse.of(borrowedUmbrella.uuid, elapsedDay))

        // when
        mockMvc.perform(
            get("/users/loggedIn/umbrella")
                .session(httpSession)
        ).andDo { println(it.response.contentAsString) }
            .andExpect(status().isOk)
            .andDo(document("find-umbrella-borrowed-by-user-doc",
                getDocumentRequest(),
                getDocumentResponse(),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("uuid").type(JsonFieldType.NUMBER)
                        .description("우산 고유번호"),
                    fieldWithPath("elapsedDay").type(JsonFieldType.NUMBER)
                        .description("대여 경과일")
                )))
    }
    @Nested
    @DisplayName("사용자는 인증 코드로 POST 요청을 보내면")
    inner class LoginTest {

        private lateinit var code: LoginCodeRequest
        private lateinit var oauthToken: OauthToken
        private lateinit var kakaoLoginResponse: KakaoLoginResponse
        private lateinit var mockHttpSession: MockHttpSession

        @BeforeEach
        fun setUp() {
            code = LoginCodeRequest(code = "1kdfjq0243f")
            oauthToken = FixtureFactory.buildOauthToken()
            kakaoLoginResponse = FixtureFactory.buildKakaoLoginResponse()
            mockHttpSession = MockHttpSession()
        }

        @Test
        @DisplayName("카카오 소셜 로그인을 할 수 있다.")
        fun loginSuccess() {
            // given
            given(oauthLoginService.getOauthToken(eq(code.code) ?: "code", any() ?: kakaoOauthInfo))
                .willReturn(oauthToken)
            given(oauthLoginService.processKakaoLogin(eq(oauthToken.accessToken) ?: "accessToken", any() ?: "loginUrl"))
                .willReturn(kakaoLoginResponse)
            given(kakaoOauthInfo.loginUri)
                .willReturn("http://kakao.login.com")

            // when & then
            mockMvc.perform(
                post("/users/oauth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(code))
                    .session(mockHttpSession)
            )
                .andDo { println(it.response.contentAsString) }
                .andExpect(status().isOk)
                .andDo(
                    document(
                        "user-login-doc",
                        getDocumentRequest(),
                        getDocumentResponse()
                    )
                )
        }

        @Test
        @DisplayName("존재하지 않는 사용자는 400 에러가 반환된다.")
        fun loginFail() {
            // given
            val kakaoUser = KakaoLoginResponse(
                id = 1L,
                kakaoAccount = KakaoAccount(
                    email = "email@email.com",
                )
            )

            given(userService.login(any<Long>() ?: 0L))
                .willThrow(NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다. 회원 가입을 해주세요."))


            val mockHttpSession = MockHttpSession()
            mockMvc = setControllerAdvice(initController(), UserExceptionHandler())

            mockHttpSession.setAttribute("kakaoUser", kakaoUser)

            // when & then
            mockMvc.perform(
                post("/users/login")
                    .content(objectMapper.writeValueAsString(code))
                    .session(mockHttpSession)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(NonExistingMemberException::class.java)
                }
        }

        @Test
        @DisplayName("유효하지 않은 로그인 코드면 400 에러를 반환한다.")
        fun invalidLoginCode() {
            // given
            given(oauthLoginService.getOauthToken(any() ?: "code", any() ?: kakaoOauthInfo))
                .willThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))

            val mockHttpSession = MockHttpSession()
            mockMvc = setControllerAdvice(initController(), UserExceptionHandler())

            // when & then
            mockMvc.perform(
                post("/users/oauth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(code))
                    .session(mockHttpSession)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(InvalidLoginCodeException::class.java)
                }
        }
    }

    @Test
    @DisplayName("사용자는 소셜 로그인 상태에서 업브렐라 로그인을 할 수 있다.")
    fun upbrellaLoginTest() {
        // given
        val kakaoUser = KakaoLoginResponse(
            id = 1L,
            kakaoAccount = KakaoAccount(
                email = "email@email.com",
            )
        )

        val session = MockHttpSession()
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        session.setAttribute("kakaoUser", kakaoUser)

        given(userService.login(any<Long>() ?: 0L))
            .willReturn(sessionUser)

        // when & then
        mockMvc.perform(
            post("/users/login")
                .session(session)
        )
            .andDo { println(it.response.contentAsString) }
            .andExpect(status().isOk)
            .andDo(
                document(
                    "user-upbrella-login-doc",
                    getDocumentRequest(),
                    getDocumentResponse()
                )
            )
    }

    @Test
    @DisplayName("사용자는 로그아웃을 할 수 있다.")
    fun logoutTest() {
        // given
        val mockHttpSession = MockHttpSession()
        mockHttpSession.setAttribute("user", FixtureBuilderFactory.builderSessionUser().sample())

        // when
        mockMvc.perform(
            post("/users/logout")
                .session(mockHttpSession)
        )
            .andDo { println(it.response.contentAsString) }
            .andExpect(status().isOk)
            .andDo(
                document(
                    "user-logout-doc",
                    getDocumentRequest(),
                    getDocumentResponse()
                )
            )

        // then
        assertThat(mockHttpSession.isInvalid).isTrue
    }

    @Nested
    @DisplayName("사용자는 소셜 로그인된 상태에서 회원가입 정보를 담아 POST 요청을 보내면")
    inner class JoinTest {

        private lateinit var joinRequest: JoinRequest
        private lateinit var mockHttpSession: MockHttpSession

        @BeforeEach
        fun setUp() {
            joinRequest = FixtureBuilderFactory.builderJoinRequest().sample()
            mockHttpSession = MockHttpSession()
        }

        @Test
        @DisplayName("사용자는 카카오 소셜 회원 가입을 할 수 있다.")
        fun joinTest() {
            // given
            val user = SessionUser(
                id = 1L,
                adminStatus = false
            )

            val kakaoUser = KakaoLoginResponse(
                id = 1L,
                kakaoAccount = KakaoAccount(
                    email = "email@email.com",
                )
            )

            mockHttpSession.setAttribute("kakaoUser", kakaoUser)

            given(userService.join(any() ?: kakaoUser, any<JoinRequest>() ?: joinRequest))
                .willReturn(user)

            // when & then
            mockMvc.perform(
                post("/users/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinRequest))
                    .session(mockHttpSession)
            )
                .andDo { println(it.response.contentAsString) }
                .andExpect(status().isOk)
                .andDo(
                    document(
                        "user-join-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                            fieldWithPath("name").description("이름"),
                            fieldWithPath("phoneNumber").description("연락처"),
                            fieldWithPath("email").optional().description("이메일"),
                            fieldWithPath("bank").optional().description("은행"),
                            fieldWithPath("accountNumber").optional().description("계좌 번호")
                        )
                    )
                )
        }

        @Test
        @DisplayName("이미 가입된 회원은 400 에러가 반환된다.")
        fun joinedMember() {
            // given
            val kakaoUser = KakaoLoginResponse(
                id = 1L,
                kakaoAccount = KakaoAccount(
                    email = "email@email.com",
                )
            )

            mockHttpSession.setAttribute("kakaoUser", kakaoUser)

            given(userService.join(any() ?: kakaoUser, any<JoinRequest>() ?: joinRequest))
                .willThrow(ExistingMemberException("[ERROR] 이미 가입된 회원입니다."))

            mockMvc = setControllerAdvice(initController(), UserExceptionHandler())

            // when & then
            mockMvc.perform(
                post("/users/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinRequest))
                    .session(mockHttpSession)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(ExistingMemberException::class.java)
                }
        }

        @Test
        @DisplayName("이미 로그인한 회원은 400 에러가 반환된다.")
        fun loginedMember() {
            // given
            mockHttpSession.setAttribute(
                "user",
                SessionUser(
                    id = 1L,
                    adminStatus = false
                ))
            mockMvc = setControllerAdvice(initController(), UserExceptionHandler())

            // when & then
            mockMvc.perform(
                post("/users/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinRequest))
                    .session(mockHttpSession)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(LoginedMemberException::class.java)
                }
        }

        @Test
        @DisplayName("소셜 로그인이 되어있지 않은 사용자는 400 에러가 반환된다.")
        fun notSocialLogined() {
            // given
            mockMvc = setControllerAdvice(initController(), UserExceptionHandler())

            // when & then
            mockMvc.perform(
                post("/users/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinRequest))
                    .session(mockHttpSession)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(NotSocialLoginedException::class.java)
                }
        }
    }

    @Test
    @DisplayName("사용자는 회원 정보 목록을 조회할 수 있다.")
    fun findUsersInfoTest() {
        // given
        val users: MutableList<User> = ArrayList()
        repeat(6) {
            users.add(FixtureBuilderFactory.builderUser(aesEncryptor).sample())
        }

        users.forEach { user -> user.createdAt = LocalDateTime.now()}
        users.forEach { user -> user.decryptData(aesEncryptor)
        }

        val allUsersInfoResponse = AllUsersInfoResponse(
            users = users.stream()
                .map { SingleUserInfoResponse.fromUser(it) }
                .toList()
        )

        given(userService.findUsers())
            .willReturn(allUsersInfoResponse)

        // when & then
        mockMvc.perform(get("/admin/users"))
            .andDo { println(it.response.contentAsString) }
            .andExpect(status().isOk)
            .andDo(
                document(
                    "find-users-info-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        fieldWithPath("users").type(JsonFieldType.ARRAY)
                            .description("회원 정보 목록"),
                        fieldWithPath("users[].id").type(JsonFieldType.NUMBER)
                            .description("사용자 고유번호"),
                        fieldWithPath("users[].name").type(JsonFieldType.STRING)
                            .description("사용자 이름"),
                        fieldWithPath("users[].phoneNumber").type(JsonFieldType.STRING)
                            .description("사용자 전화번호"),
                        fieldWithPath("users[].email").type(JsonFieldType.STRING)
                            .description("사용자 이메일"),
                        fieldWithPath("users[].bank").type(JsonFieldType.STRING)
                            .optional()
                            .description("은행 이름"),
                        fieldWithPath("users[].accountNumber").type(JsonFieldType.STRING)
                            .optional()
                            .description("사용자 계좌 번호"),
                        fieldWithPath("users[].adminStatus").type(JsonFieldType.BOOLEAN)
                            .description("관리자 여부"),
                        fieldWithPath("users[].createdAt").type(JsonFieldType.ARRAY)
                            .description("사용자 생성일")

                    )
                )
            )
    }

    @Test
    @DisplayName("로그인된 사용자는 우산 대여 정보를 조회할 수 있다.")
    fun readUserHistoriesTest() {
        // given
        val historyResponses: MutableList<SingleHistoryResponse> = ArrayList()
        repeat(5) {
            historyResponses.add(FixtureBuilderFactory.builderSingleHistoryResponse().sample())
        }

        val historyResponse = AllHistoryResponse.of(historyResponses)

        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        val mockHttpSession = MockHttpSession()
        mockHttpSession.setAttribute("user", sessionUser)

        given(rentService.findAllHistoriesByUser(sessionUser.id))
            .willReturn(historyResponse)

        // when & then
        mockMvc.perform(
            get("/users/histories")
                .session(mockHttpSession)
        )
            .andDo { println(it.response.contentAsString) }
            .andExpect(status().isOk)
            .andDo(
                document(
                    "user-history-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        fieldWithPath("histories[]").type(JsonFieldType.ARRAY)
                            .description("우산 대여 목록"),
                        fieldWithPath("histories[].umbrellaUuid").type(JsonFieldType.NUMBER)
                            .description("우산 관리 번호"),
                        fieldWithPath("histories[].rentedAt").type(JsonFieldType.STRING)
                            .description("대여 날짜"),
                        fieldWithPath("histories[].rentedStore").type(JsonFieldType.STRING)
                            .description("대여 협업 지점명"),
                        fieldWithPath("histories[].returnAt").type(JsonFieldType.STRING)
                            .description("반납한 날짜 혹은 반납 기한"),
                        fieldWithPath("histories[].isReturned").type(JsonFieldType.BOOLEAN)
                            .description("우산 반납 여부"),
                        fieldWithPath("histories[].isRefunded").type(JsonFieldType.BOOLEAN)
                            .description("우산 환급 여부")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 은행 정보를 수정할 수 있다.")
    fun updateUserBankAccount() {
        // given
        val updateBankAccountRequest = FixtureBuilderFactory.builderBankAccount().sample()
        val mockHttpSession = MockHttpSession()
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()

        mockHttpSession.setAttribute("user", sessionUser)

        // when & then
        mockMvc.perform(
            patch("/users/bankAccount")
                .session(mockHttpSession)
                .content(objectMapper.writeValueAsString(updateBankAccountRequest))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo { println(it.response.contentAsString) }
            .andDo(
                document(
                    "update-user-bank-account-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestFields(
                        fieldWithPath("bank").type(JsonFieldType.STRING)
                            .description("은행 이름"),
                        fieldWithPath("accountNumber").type(JsonFieldType.STRING)
                            .description("계좌 번호")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자가 회원탈퇴를 하면, 삭제된 회원 정보로 변경되고 회원은 탈퇴된다.")
    fun deleteUserTest() {
        // given
        val mockHttpSession = MockHttpSession()
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        mockHttpSession.setAttribute("user", sessionUser)

        // when & then
        mockMvc.perform(
            delete("/users/loggedIn")
                .session(mockHttpSession)
        )
            .andExpect(status().isOk)
            .andDo { println(it.response.contentAsString) }
            .andDo(
                document(
                    "delete-user-doc",
                    getDocumentRequest(),
                    getDocumentResponse()
                )
            )
    }

    @Test
    @DisplayName("관리자가 회원탈퇴 시키면, 블랙리스트에 추가되고 회원탈퇴가 된다.")
    fun withdrawUserTest() {
        // given
        val mockHttpSession = MockHttpSession()
        mockHttpSession.setAttribute("userId", 70L)
        val userId = 1L

        // when & then
        mockMvc.perform(
            delete("/admin/users/{userId}", userId)
                .session(mockHttpSession)
        )
            .andExpect(status().isOk)
            .andDo { println(it.response.contentAsString) }
            .andDo(
                document(
                    "withdraw-user-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("userId").description("회원 고유번호")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 자신의 계좌정보를 삭제할 수 있다.")
    fun deleteBackAccountTest() {
        // given
        val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
        val mockHttpSession = MockHttpSession()
        mockHttpSession.setAttribute("user", sessionUser)

        // when
        doNothing().`when`(userService).deleteUserBankAccount(sessionUser.id)

        // then
        mockMvc.perform(
            delete("/users/bankAccount")
                .session(mockHttpSession)
        )
            .andExpect(status().isOk)
            .andDo { println(it.response.contentAsString) }
            .andDo(
                document(
                    "delete-user-bank-account-doc",
                    getDocumentRequest(),
                    getDocumentResponse()
                )
            )
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 조회할 수 있다.")
    fun findAllBlackListTest() {
        // given
        val blackLists = AllBlackListResponse(
            blackList = listOf(
                SingleBlackListResponse(
                    id = 1L,
                    blockedAt = LocalDateTime.now(),
                )
            )
        )

        given(blackListService.findBlackList())
            .willReturn(blackLists)

        // when & then
        mockMvc.perform(
            get("/users/blackList")
        )
            .andDo { println(it.response.contentAsString) }
            .andExpect(status().isOk)
            .andDo(
                document(
                    "find-all-black-list-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        fieldWithPath("blackList[]").type(JsonFieldType.ARRAY)
                            .description("블랙리스트 목록"),
                        fieldWithPath("blackList[].id").type(JsonFieldType.NUMBER)
                            .description("블랙리스트 고유번호"),
                        fieldWithPath("blackList[].blockedAt").description("블랙리스트 등록 날짜")
                    )
                )
            )
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 삭제할 수 있다.")
    fun deleteBlackListTest() {
        // given
        val blackListId = 1L
        doNothing().`when`(blackListService).deleteBlackList(blackListId)

        // when & then
        mockMvc.perform(
            delete("/users/blackList/{blackListId}", blackListId)
        )
            .andDo { println(it.response.contentAsString) }
            .andExpect(status().isOk)
            .andDo(
                document(
                    "delete-black-list-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("blackListId").description("블랙리스트 고유번호")
                    )
                )
            )
    }

    @Test
    @DisplayName("관리자가 회원의 관리자 상태를 변경할 수 있다.")
    fun updateAdminStatusTest() {
        // given
        val userId = 1L
        doNothing().`when`(userService).updateAdminStatus(userId)

        // when & then
        mockMvc.perform(
            patch("/admin/users/{userId}", userId)
        )
            .andDo { println(it.response.contentAsString) }
            .andExpect(status().isOk)
            .andDo(
                document(
                    "update-admin-status-doc",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("userId").description("회원 고유번호")
                    )
                )
            )
    }
}