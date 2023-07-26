package upbrella.be.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.login.dto.request.LoginCodeRequest;
import upbrella.be.login.dto.response.Properties;
import upbrella.be.login.dto.response.NaverLoggedInUser;
import upbrella.be.login.dto.response.LoggedInUserResponse;
import upbrella.be.login.dto.token.*;
import upbrella.be.login.service.OauthLoginService;
import upbrella.be.user.service.UserService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/oauth")
public class LoginController {

    private final OauthLoginService oauthLoginService;
    private final UserService userService;
    private final CommonOauthInfo kakaoOauthInfo;
    private final CommonOauthInfo naveroauthInfo;

    public LoginController(OauthLoginService oauthLoginService, UserService userService, KakaoOauthInfo kakaoOauthInfo, NaverOauthInfo naveroauthInfo) {
        this.oauthLoginService = oauthLoginService;
        this.userService = userService;
        this.kakaoOauthInfo = kakaoOauthInfo;
        this.naveroauthInfo = naveroauthInfo;
    }

    @PostMapping("/kakao")
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> kakaoLogin(HttpSession session, @RequestBody LoginCodeRequest code) {

        OauthToken kakaoAccessToken = oauthLoginService.getOauthToken(code.getCode(), kakaoOauthInfo);
        Properties kakaoLoggedInUser = oauthLoginService.processKakaoLogin(kakaoAccessToken.getAccessToken(), kakaoOauthInfo.getLoginUri());
        // TODO: 비즈앱으로 전환 후 로그인 처리하기

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 로그인 성공",
                        LoggedInUserResponse.builder()
                                .id(1L)
                                .name("카카오 사용자")
                                .phoneNumber("010-0000-0000")
                                .adminStatus(false)
                                .build()));
    }

    @PostMapping("/naver")
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> naverLogin(HttpSession session, @RequestBody LoginCodeRequest code) {

        OauthToken naverAccessToken = oauthLoginService.getOauthToken(code.getCode(), naveroauthInfo);
        NaverLoggedInUser loggedInUser = oauthLoginService.processNaverLogin(naverAccessToken.getAccessToken(), naveroauthInfo.getLoginUri());
        LoggedInUserResponse loggedInUserResponse = userService.joinService(loggedInUser.getName(), loggedInUser.getMobile());
        session.setAttribute("userId", loggedInUserResponse.getId());

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "네이버 로그인 성공",
                        loggedInUserResponse));
    }
}
