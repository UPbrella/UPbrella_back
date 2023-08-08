package upbrella.be.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.rent.service.RentService;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.response.*;
import upbrella.be.user.dto.token.KakaoOauthInfo;
import upbrella.be.user.dto.token.OauthToken;
import upbrella.be.user.entity.User;
import upbrella.be.user.exception.*;
import upbrella.be.user.service.OauthLoginService;
import upbrella.be.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
    private RentRepository rentRepository;
    @Mock
    private RentService rentService;

    @Override
    protected Object initController() {
        return new UserController(oauthLoginService, userService, kakaoOauthInfo, rentRepository, rentService);
    }

    @DisplayName("사용자는 로그인된 유저 정보를 조회할 수 있다.")
    @Test
    void findUserInfoTest() throws Exception {

        // when & then
        mockMvc.perform(
                        get("/users/loggedIn")
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
                                        .description("사용자 전화번호")
                        )));
    }

    @Test
    @DisplayName("사용자는 유저가 빌린 우산을 조회할 수 있다.")
    void findUmbrellaBorrowedByUserTest() throws Exception {
        // given
        Umbrella borrowedUmbrella = FixtureBuilderFactory.builderUmbrella().sample();
        History rentalHistory = FixtureFactory.buildHistoryWithUmbrella(borrowedUmbrella);

        given(rentRepository.findByUserAndReturnedAtIsNull(anyLong()))
                .willReturn(Optional.ofNullable(rentalHistory));

        // when
        mockMvc.perform(
                        get("/users/loggedIn/umbrella")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find-umbrella-borrowed-by-user-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호")
                        )));
    }

    @Nested
    @DisplayName("사용자는 인증 코드로 GET 요청을 보내면")
    class LoginTest {

        private String code;
        private OauthToken oauthToken;
        private KakaoLoginResponse kakaoLoginResponse;
        private long userId;
        private MockHttpSession mockHttpSession = new MockHttpSession();

        @BeforeEach
        void setUp() {
            code = "1kdfjq0243f";
            oauthToken = FixtureFactory.buildOauthToken();
            kakaoLoginResponse = FixtureFactory.buildKakaoLoginResponse();
            userId = FixtureBuilderFactory.buildLong();
        }

        @Test
        @DisplayName("카카오 소셜 로그인을 할 수 있다.")
        void loginSuccess() throws Exception {

            // given
            given(oauthLoginService.getOauthToken(eq(code), any()))
                    .willReturn(oauthToken);
            given(oauthLoginService.processKakaoLogin(eq(oauthToken.getAccessToken()), any()))
                    .willReturn(kakaoLoginResponse);
            given(kakaoOauthInfo.getLoginUri())
                    .willReturn("http://kakao.login.com");
            given(userService.login(kakaoLoginResponse.getId()))
                    .willReturn(userId);

            // when
            mockMvc.perform(
                            get("/users/login")
                                    .param("code", code)
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

            // given
            given(oauthLoginService.getOauthToken(eq(code), any()))
                    .willReturn(oauthToken);
            given(oauthLoginService.processKakaoLogin(eq(oauthToken.getAccessToken()), any()))
                    .willReturn(kakaoLoginResponse);
            given(kakaoOauthInfo.getLoginUri())
                    .willReturn("http://kakao.login.com");
            given(userService.login(any()))
                    .willThrow(new NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다. 회원 가입을 해주세요."));
            MockHttpSession mockHttpSession = new MockHttpSession();

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UserExceptionHandler());

            // when
            mockMvc.perform(
                            get("/users/login")
                                    .param("code", code)
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
                            get("/users/login")
                                    .param("code", code)
                                    .session(mockHttpSession))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(InvalidLoginCodeException.class));
        }
    }

    @Nested
    @DisplayName("사용자는 소셜 로그인된 상태에서 회원가입 정보를 담아 POST 요청을 보내면")
    class JoinTest {

        private JoinRequest joinRequest;
        private OauthToken oauthToken;
        private MockHttpSession mockHttpSession;
        private KakaoLoginResponse kakaoLoginResponse;
        private long userId;

        @BeforeEach
        void setUp() {

            joinRequest = FixtureBuilderFactory.builderJoinRequest().sample();
            oauthToken = FixtureFactory.buildOauthToken();
            kakaoLoginResponse = FixtureFactory.buildKakaoLoginResponse();
            userId = FixtureBuilderFactory.buildLong();
            mockHttpSession = new MockHttpSession();
        }

        @Test
        @DisplayName("사용자는 카카오 소셜 회원 가입을 할 수 있다.")
        void joinTest() throws Exception {

            // given
            mockHttpSession.setAttribute("authToken", oauthToken);
            given(oauthLoginService.processKakaoLogin(anyString(), anyString()))
                    .willReturn(kakaoLoginResponse);
            given(kakaoOauthInfo.getLoginUri())
                    .willReturn("http://kakao.login.com");
            given(userService.join(eq(kakaoLoginResponse.getId()), any(JoinRequest.class)))
                    .willReturn(userId);

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
            mockHttpSession.setAttribute("authToken", oauthToken);
            given(oauthLoginService.processKakaoLogin(anyString(), anyString()))
                    .willReturn(kakaoLoginResponse);
            given(kakaoOauthInfo.getLoginUri())
                    .willReturn("http://kakao.login.com");
            given(userService.join(eq(kakaoLoginResponse.getId()), any(JoinRequest.class)))
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
            mockHttpSession.setAttribute("userId", userId);

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
            users.add(FixtureBuilderFactory.builderUser().sample());
        }

        AllUsersInfoResponse allUsersInfoResponse = AllUsersInfoResponse.builder()
                .users(users.stream()
                        .map(SingleUserInfoResponse::fromUser)
                        .collect(Collectors.toList()))
                .build();

        given(userService.findUsers())
                .willReturn(allUsersInfoResponse);

        // when
        mockMvc.perform(
                        get("/users")
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
                                fieldWithPath("users[].socialId").type(JsonFieldType.NUMBER)
                                        .description("사용자 소셜 고유번호"),
                                fieldWithPath("users[].name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("users[].phoneNumber").type(JsonFieldType.STRING)
                                        .description("사용자 전화번호"),
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

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("userId", 70L);
        given(rentService.findAllHistoriesByUser(70L))
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
}
