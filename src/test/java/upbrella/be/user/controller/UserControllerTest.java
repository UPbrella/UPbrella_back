package upbrella.be.user.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.client.HttpClientErrorException;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.config.FixtureFactory;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.service.RentService;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.request.KakaoAccount;
import upbrella.be.user.dto.request.LoginCodeRequest;
import upbrella.be.user.dto.request.UpdateBankAccountRequest;
import upbrella.be.user.dto.response.*;
import upbrella.be.user.dto.token.KakaoOauthInfo;
import upbrella.be.user.dto.token.OauthToken;
import upbrella.be.user.entity.User;
import upbrella.be.user.exception.*;
import upbrella.be.user.service.OauthLoginService;
import upbrella.be.user.service.UserService;
import upbrella.be.util.AesEncryptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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
public class UserControllerTest extends RestDocsSupport {

    @Mock
    private OauthLoginService oauthLoginService;
    @Mock
    private UserService userService;
    @Mock
    private KakaoOauthInfo kakaoOauthInfo;
    @Mock
    private RentService rentService;
    @Mock
    private AesEncryptor aesEncryptor;

    @Override
    protected Object initController() {
        return new UserController(oauthLoginService, userService, kakaoOauthInfo, rentService);
    }

    @DisplayName("사용자는 로그인된 유저 정보를 조회할 수 있다.")
    @Test
    void findUserInfoTest() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().sample();
        User user = FixtureBuilderFactory.builderUser(aesEncryptor).sample();

        session.setAttribute("user", sessionUser);
        User decryptedUser = user.decryptData(aesEncryptor);
        given(userService.findDecryptedUserById(sessionUser))
                .willReturn(decryptedUser);

        // when & then
        mockMvc.perform(
                        get("/users/loggedIn")
                                .session(session)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find-user-info-doc",
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
                                        .optional()
                        )));
    }

    @Test
    @DisplayName("사용자는 유저가 빌린 우산을 조회할 수 있다.")
    void findUmbrellaBorrowedByUserTest() throws Exception {
        // given
        MockHttpSession httpSession = new MockHttpSession();
        SessionUser user = FixtureBuilderFactory.builderSessionUser().sample();

        httpSession.setAttribute("user", user);

        Umbrella borrowedUmbrella = FixtureBuilderFactory.builderUmbrella().sample();
        History history = FixtureBuilderFactory.builderHistory(aesEncryptor).sample();
        int elapsedDay = LocalDateTime.now().getDayOfYear() - history.getRentedAt().getDayOfYear();

        given(userService.findUmbrellaBorrowedByUser(user))
                .willReturn(UmbrellaBorrowedByUserResponse.of(borrowedUmbrella.getUuid(), elapsedDay));

        // when
        mockMvc.perform(
                        get("/users/loggedIn/umbrella")
                                .session(httpSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find-umbrella-borrowed-by-user-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("elapsedDay").type(JsonFieldType.NUMBER)
                                        .description("대여 경과일")
                        )));
    }

    @Nested
    @DisplayName("사용자는 인증 코드로 POST 요청을 보내면")
    class LoginTest {

        private LoginCodeRequest code;
        private OauthToken oauthToken;
        private KakaoLoginResponse kakaoLoginResponse;
        private MockHttpSession mockHttpSession = new MockHttpSession();

        @BeforeEach
        void setUp() {
            code = LoginCodeRequest.builder().code("1kdfjq0243f").build();
            oauthToken = FixtureFactory.buildOauthToken();
            kakaoLoginResponse = FixtureFactory.buildKakaoLoginResponse();
        }

        @Test
        @DisplayName("카카오 소셜 로그인을 할 수 있다.")
        void loginSuccess() throws Exception {

            // given
            given(oauthLoginService.getOauthToken(eq(code.getCode()), any()))
                    .willReturn(oauthToken);
            given(oauthLoginService.processKakaoLogin(eq(oauthToken.getAccessToken()), any()))
                    .willReturn(kakaoLoginResponse);
            given(kakaoOauthInfo.getLoginUri())
                    .willReturn("http://kakao.login.com");

            // when
            mockMvc.perform(
                            post("/users/oauth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(code))
                                    .session(mockHttpSession)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("user-login-doc",
                            getDocumentRequest(),
                            getDocumentResponse()
                    ));
        }

        @Test
        @DisplayName("존재하지 않는 사용자는 400 에러가 반환된다.")
        void loginFail() throws Exception {

            KakaoLoginResponse kakaoUser = KakaoLoginResponse.builder()
                    .id(1L)
                    .kakaoAccount(
                            KakaoAccount.builder()
                                    .email("email@email.com")
                                    .build())
                    .build();

            given(userService.login(any()))
                    .willThrow(new NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다. 회원 가입을 해주세요."));
            MockHttpSession mockHttpSession = new MockHttpSession();

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UserExceptionHandler());
            mockHttpSession.setAttribute("kakaoUser", kakaoUser);
            // when
            mockMvc.perform(
                            post("/users/login")
                                    .content(objectMapper.writeValueAsString(code))
                                    .session(mockHttpSession))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(NonExistingMemberException.class));
        }

        @Test
        @DisplayName("유효하지 않은 로그인 코드면 400 에러를 반환한다.")
        void invalidLoginCode() throws Exception {

            // given
            given(oauthLoginService.getOauthToken(any(), any()))
                    .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
            MockHttpSession mockHttpSession = new MockHttpSession();

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UserExceptionHandler());

            // when
            mockMvc.perform(
                            post("/users/oauth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsBytes(code))
                                    .session(mockHttpSession))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(InvalidLoginCodeException.class));
        }
    }

    @Test
    @DisplayName("사용자는 소셜 로그인 상태에서 업브렐라 로그인을 할 수 있다.")
    void upbrellaLoginTest() throws Exception {
        // given

        KakaoLoginResponse kakaoUser = KakaoLoginResponse.builder()
                .id(1L)
                .kakaoAccount(
                        KakaoAccount.builder()
                                .email("email@email.com")
                                .build())
                .build();

        MockHttpSession session = new MockHttpSession();

        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().sample();
        session.setAttribute("kakaoUser", kakaoUser);
        given(userService.login(any())).willReturn(sessionUser);

        // when

        // then
        mockMvc.perform(
                        post("/users/login")
                                .session(session)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-upbrella-login-doc",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }


    @Test
    @DisplayName("사용자는 로그아웃을 할 수 있다.")
    void logoutTest() throws Exception {

        // given
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", FixtureBuilderFactory.builderSessionUser().sample());

        // when
        mockMvc.perform(
                        post("/users/logout")
                                .session(mockHttpSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-logout-doc",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));

        // then
        assertThat(mockHttpSession.isInvalid()).isEqualTo(true);
    }

    @Nested
    @DisplayName("사용자는 소셜 로그인된 상태에서 회원가입 정보를 담아 POST 요청을 보내면")
    class JoinTest {

        private JoinRequest joinRequest;
        private MockHttpSession mockHttpSession;

        @BeforeEach
        void setUp() {

            joinRequest = FixtureBuilderFactory.builderJoinRequest().sample();
            mockHttpSession = new MockHttpSession();
        }

        @Test
        @DisplayName("사용자는 카카오 소셜 회원 가입을 할 수 있다.")
        void joinTest() throws Exception {

            // given
            SessionUser user = SessionUser.builder()
                    .id(1L)
                    .adminStatus(false)
                    .build();

            KakaoLoginResponse kakaoUser = KakaoLoginResponse.builder()
                    .id(1L)
                    .kakaoAccount(
                            KakaoAccount.builder()
                                    .email("email@email.com")
                                    .build())
                    .build();

            mockHttpSession.setAttribute("kakaoUser", kakaoUser);
            given(userService.join(any(KakaoLoginResponse.class), any(JoinRequest.class)))
                    .willReturn(user);

            // when
            mockMvc.perform(
                            post("/users/join")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(joinRequest))
                                    .session(mockHttpSession)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("user-join-doc",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("name")
                                            .description("이름"),
                                    fieldWithPath("phoneNumber")
                                            .description("연락처"),
                                    fieldWithPath("bank")
                                            .optional()
                                            .description("은행"),
                                    fieldWithPath("accountNumber")
                                            .optional()
                                            .description("계좌 번호")
                            )));
        }

        @Test
        @DisplayName("이미 가입된 회원은 400 에러가 반환된다.")
        void joinedMember() throws Exception {

            // given

            KakaoLoginResponse kakaoUser = KakaoLoginResponse.builder()
                    .id(1L)
                    .kakaoAccount(
                            KakaoAccount.builder()
                                    .email("email@email.com")
                                    .build())
                    .build();

            mockHttpSession.setAttribute("kakaoUser", kakaoUser);
            given(userService.join(any(), any(JoinRequest.class)))
                    .willThrow(new ExistingMemberException("[ERROR] 이미 가입된 회원입니다."));

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UserExceptionHandler());

            // when
            mockMvc.perform(
                            post("/users/join")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(joinRequest))
                                    .session(mockHttpSession))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(ExistingMemberException.class));
        }

        @Test
        @DisplayName("이미 로그인한 회원은 400 에러가 반환된다.")
        void loginedMember() throws Exception {

            // given
            mockHttpSession.setAttribute("user", SessionUser.builder().build());

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UserExceptionHandler());

            // when
            mockMvc.perform(
                            post("/users/join")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(joinRequest))
                                    .session(mockHttpSession))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(LoginedMemberException.class));
        }

        @Test
        @DisplayName("소셜 로그인이 되어있지 않은 사용자는 400 에러가 반환된다.")
        void notSocialLogined() throws Exception {

            // given
            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UserExceptionHandler());

            // when
            mockMvc.perform(
                            post("/users/join")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(joinRequest))
                                    .session(mockHttpSession))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(NotSocialLoginedException.class));
        }
    }

    @DisplayName("사용자는 회원 정보 목록을 조회할 수 있다.")
    @Test
    void findUsersInfoTest() throws Exception {

        // given
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            users.add(FixtureBuilderFactory.builderUser(aesEncryptor).sample());
        }

        AllUsersInfoResponse allUsersInfoResponse = AllUsersInfoResponse.builder()
                .users(users.stream()
                        .map(user -> user.decryptData(aesEncryptor))
                        .map(SingleUserInfoResponse::fromUser)
                        .collect(Collectors.toList()))
                .build();

        given(userService.findUsers())
                .willReturn(allUsersInfoResponse);

        // when
        mockMvc.perform(
                        get("/admin/users")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find-users-info-doc",
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
                                        .description("관리자 여부")
                        )));
    }

    @Test
    @DisplayName("로그인된 사용자는 우산 대여 정보를 조회할 수 있다.")
    void readUserHistoriesTest() throws Exception {

        // given
        List<SingleHistoryResponse> historyResponses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            historyResponses.add(FixtureBuilderFactory.builderSingleHistoryResponse().sample());
        }

        AllHistoryResponse historyResponse = AllHistoryResponse.of(historyResponses);

        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().sample();
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", sessionUser);
        given(rentService.findAllHistoriesByUser(sessionUser.getId()))
                .willReturn(historyResponse);

        // when & then
        mockMvc.perform(
                        get("/users/histories")
                                .session(mockHttpSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-history-doc",
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
                                fieldWithPath("histories[].returned").type(JsonFieldType.BOOLEAN)
                                        .description("우산 반납 여부"),
                                fieldWithPath("histories[].refunded").type(JsonFieldType.BOOLEAN)
                                        .description("우산 환급 여부")
                        )));
    }

    @Test
    @DisplayName("사용자는 은행 정보를 수정할 수 있다.")
    void updateUserBankAccount() throws Exception {

        // given
        UpdateBankAccountRequest updateBankAccountRequest = FixtureBuilderFactory.builderBankAccount().sample();
        MockHttpSession mockHttpSession = new MockHttpSession();
        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().sample();
        mockHttpSession.setAttribute("user", sessionUser);

        // when

        // then
        mockMvc.perform(
                        patch("/users/bankAccount")
                                .session(mockHttpSession)
                                .content(objectMapper.writeValueAsString(updateBankAccountRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-user-bank-account-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("bank").type(JsonFieldType.STRING)
                                        .description("은행 이름"),
                                fieldWithPath("accountNumber").type(JsonFieldType.STRING)
                                        .description("계좌 번호")
                        )));
    }

    @Test
    @DisplayName("사용자가 회원탈퇴를 하면, 삭제된 회원 정보로 변경되고 회원은 탈퇴된다.")
    void deleteUserTest() throws Exception {
        // given
        MockHttpSession mockHttpSession = new MockHttpSession();
        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().sample();
        mockHttpSession.setAttribute("user", sessionUser);

        // when

        // then
        mockMvc.perform(
                        delete("/users/loggedIn")
                                .session(mockHttpSession)
                ).andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-user-doc",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("관리자가 회원탈퇴 시키면, 블랙리스트에 추가되고 회원탈퇴가 된다.")
    void withdrawUserTest() throws Exception {
        // given
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("userId", 70L);
        long userId = 1L;

        // when


        // then
        mockMvc.perform(
                        delete("/admin/users/{userId}", userId)
                                .session(mockHttpSession)
                ).andExpect(status().isOk())
                .andDo(print())
                .andDo(document("withdraw-user-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("userId").description("회원 고유번호")
                        )));

    }

    @Test
    @DisplayName("사용자는 자신의 계좌정보를 삭제할 수 있다.")
    void deleteBackAccountTest() throws Exception {
        // given
        SessionUser sessionUser = FixtureBuilderFactory.builderSessionUser().sample();
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", sessionUser);

        // when
        doNothing().when(userService).deleteUserBankAccount(sessionUser.getId());

        // then
        mockMvc.perform(
                        delete("/users/bankAccount")
                                .session(mockHttpSession)
                ).andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-user-bank-account-doc",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 조회할 수 있다.")
    void findAllBlackListTest() throws Exception {
        // given
        AllBlackListResponse blackLists = AllBlackListResponse.builder()
                .blackList(List.of(SingleBlackListResponse.builder()
                        .id(1L)
                        .blockedAt(LocalDateTime.now())
                        .build()))
                .build();

        // when
        given(userService.findBlackList())
                .willReturn(blackLists);

        // then
        mockMvc.perform(
                        get("/users/blackList")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find-all-black-list-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("blackList[]").type(JsonFieldType.ARRAY)
                                        .description("블랙리스트 목록"),
                                fieldWithPath("blackList[].id").type(JsonFieldType.NUMBER)
                                        .description("블랙리스트 고유번호"),
                                fieldWithPath("blackList[].blockedAt")
                                        .description("블랙리스트 등록 날짜")
                        )));
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 삭제할 수 있다.")
    void deleteBlackListTest() throws Exception {
        // given
        long blackListId = 1L;

        // when
        doNothing().when(userService).deleteBlackList(blackListId);

        // then
        mockMvc.perform(
                        delete("/users/blackList/{blackListId}", blackListId)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete-black-list-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("blackListId").description("블랙리스트 고유번호")
                        )));

    }

    @Test
    @DisplayName("관리자가 회원의 관리자 상태를 변경할 수 있다.")
    void updateAdminStatusTest() throws Exception {
        // given
        long userId = 1L;
        doNothing().when(userService).updateAdminStatus(userId);

        // when

        // then
        mockMvc.perform(
                        patch("/admin/users/{userId}", userId)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update-admin-status-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("userId").description("회원 고유번호")
                        )));
    }
}
