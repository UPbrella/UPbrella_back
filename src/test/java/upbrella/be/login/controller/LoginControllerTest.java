package upbrella.be.login.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.login.dto.request.NaverLoginCodeRequest;
import upbrella.be.login.dto.response.NaverLoggedInUser;
import upbrella.be.login.dto.response.LoggedInUserResponse;
import upbrella.be.login.dto.token.NaverToken;
import upbrella.be.login.service.OauthLoginService;
import upbrella.be.user.service.UserService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

public class LoginControllerTest extends RestDocsSupport {

    private final OauthLoginService oauthLoginService = mock(OauthLoginService.class);
    private final UserService userService = mock(UserService.class);

    @Override
    protected Object initController() {
        return new LoginController(oauthLoginService, userService);
    }

    @Test
    @DisplayName("사용자는 카카오 소셜 로그인을 할 수 있다.")
    void kakoLoginTest() throws Exception {
        // given
        String code = "{\"code\":\"1kdfjq0243f\"}";


        // when
        mockMvc.perform(
                        post("/oauth/kakao")
                                .content(code)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("kakao-login-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("code")
                                        .description("카카오 로그인 인증 코드")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("사용자 고유번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                                        .description("사용자 전화번호"),
                                fieldWithPath("adminStatus").type(JsonFieldType.BOOLEAN)
                                        .description("관리자 여부")
                        )));
    }

    @Test
    @DisplayName("사용자는 네이버 소셜 로그인을 할 수 있다.")
    void naverLoginTest() throws Exception {
        // given
        NaverLoginCodeRequest request = NaverLoginCodeRequest.builder()
                .code("1kdfjq0243f")
                .state("upbrellaDev")
                .build();

        given(oauthLoginService.getAccessToken(anyString(), anyString()))
                .willReturn(new NaverToken("accessToken", "refreshToken", "tokenType", 3600L));
        given(oauthLoginService.processLogin(anyString()))
                .willReturn(new NaverLoggedInUser("네이버 사용자", "010-0000-0000"));
        given(userService.joinService(anyString(), anyString()))
                .willReturn(LoggedInUserResponse.builder()
                        .id(1L)
                        .name("네이버 사용자")
                        .phoneNumber("010-0000-0000")
                        .adminStatus(false)
                        .build());

        // when
        mockMvc.perform(
                        post("/oauth/naver")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("naver-login-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("code").description("네이버 로그인 인증 코드"),
                                fieldWithPath("state").description("네이버 로그인 상태 검증 코드")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("사용자 고유번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                                        .description("사용자 전화번호"),
                                fieldWithPath("adminStatus").type(JsonFieldType.BOOLEAN)
                                        .description("관리자 여부")
                        )));
    }
}
