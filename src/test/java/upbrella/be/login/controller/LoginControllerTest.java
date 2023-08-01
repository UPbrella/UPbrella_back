package upbrella.be.login.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.login.dto.request.JoinRequest;
import upbrella.be.login.dto.response.KakaoLoginResponse;
import upbrella.be.login.dto.token.KakaoOauthInfo;
import upbrella.be.login.dto.token.OauthToken;
import upbrella.be.login.service.OauthLoginService;
import upbrella.be.user.service.UserService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

public class LoginControllerTest extends RestDocsSupport {

    private OauthLoginService oauthLoginService = mock(OauthLoginService.class);
    private UserService userService = mock(UserService.class);
    private KakaoOauthInfo kakaoOauthInfo = mock(KakaoOauthInfo.class);

    @Override
    protected Object initController() {
        return new LoginController(oauthLoginService, userService, kakaoOauthInfo);
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
                        get("/oauth/login")
                                .content(code)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(mockHttpSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("kakao-login-doc",
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
        String code = "{\"code\":\"1kdfjq0243f\"}";
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
                        post("/oauth/join")
                                .content(code)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(mockHttpSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("kakao-login-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("code")
                                        .description("카카오 로그인 인증 코드")
                        )
                ));
    }
}
