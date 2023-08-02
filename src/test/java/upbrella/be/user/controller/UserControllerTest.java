package upbrella.be.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.response.KakaoLoginResponse;
import upbrella.be.user.dto.response.UserInfoResponse;
import upbrella.be.user.dto.token.KakaoOauthInfo;
import upbrella.be.user.dto.token.OauthToken;
import upbrella.be.user.service.OauthLoginService;
import upbrella.be.user.service.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Override
    protected Object initController() {
        return new UserController(oauthLoginService, userService, kakaoOauthInfo, rentRepository);
    }

    @DisplayName("사용자는 유저 정보를 조회할 수 있다.")
    @Test
    void findUserInfoTest() throws Exception {

        // given
        UserInfoResponse user = UserInfoResponse.builder()
                .id(1)
                .name("일반사용자")
                .phoneNumber("010-0000-0000")
                .build();

        // when
        mockMvc.perform(
                        get("/users")
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
        Umbrella borrowedUmbrella = Umbrella.builder()
                .id(1L)
                .uuid(1L)
                .build();

        History rentalHistory = History.builder()
                .id(1L)
                .umbrella(borrowedUmbrella)
                .build();

        given(rentRepository.findByUserAndReturnedAtIsNull(anyLong()))
                .willReturn(Optional.ofNullable(rentalHistory));

        // when
        mockMvc.perform(
                        get("/users/umbrella")
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

    @Test
    @DisplayName("사용자는 카카오 소셜 로그인을 할 수 있다.")
    void LoginTest() throws Exception {
        // given
        String code = "{\"code\":\"1kdfjq0243f\"}";
        OauthToken oauthToken = new OauthToken("a", "b", "c", 100L);
        given(oauthLoginService.getOauthToken(any(), any()))
                .willReturn(oauthToken);
        given(oauthLoginService.processKakaoLogin(any(), any()))
                .willReturn(KakaoLoginResponse.builder().id(2L).build());
        given(kakaoOauthInfo.getLoginUri())
                .willReturn("http://kakao.login.com");
        given(userService.login(any()))
                .willReturn(2L);
        MockHttpSession mockHttpSession = new MockHttpSession();

        // when
        mockMvc.perform(
                        get("/users/login")
                                .content(code)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(mockHttpSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-login-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("code")
                                        .description("카카오 로그인 인증 코드")
                        )
                ));
    }

    @Test
    @DisplayName("사용자는 카카오 소셜 회원 가입을 할 수 있다.")
    void joinTest() throws Exception {
        // given
        JoinRequest joinRequest = JoinRequest.builder()
                .name("홍길동")
                .bank("신한")
                .accountNumber("110-421-674103")
                .phoneNumber("010-2084-3478")
                .build();

        OauthToken oauthToken = new OauthToken("a", "b", "c", 100L);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("authToken", oauthToken);
        given(oauthLoginService.processKakaoLogin(anyString(), anyString()))
                .willReturn(KakaoLoginResponse.builder().id(2L).build());
        given(kakaoOauthInfo.getLoginUri())
                .willReturn("http://kakao.login.com");
        given(userService.join(eq(2L), any(JoinRequest.class)))
                .willReturn(2L);

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
                                )
                ));
    }
}
