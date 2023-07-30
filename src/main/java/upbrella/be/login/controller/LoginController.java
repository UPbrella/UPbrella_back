package upbrella.be.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.login.dto.request.JoinRequest;
import upbrella.be.login.dto.response.KakaoLoginResponse;
import upbrella.be.login.dto.response.LoggedInUserResponse;
import upbrella.be.login.dto.token.CommonOauthInfo;
import upbrella.be.login.dto.token.KakaoOauthInfo;
import upbrella.be.login.dto.token.OauthToken;
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

    public LoginController(OauthLoginService oauthLoginService, UserService userService, KakaoOauthInfo kakaoOauthInfo) {
        this.oauthLoginService = oauthLoginService;
        this.userService = userService;
        this.kakaoOauthInfo = kakaoOauthInfo;
    }

    @GetMapping("/kakao")
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> kakaoLoginDev(HttpSession session, String code) {

        OauthToken kakaoAccessToken = oauthLoginService.getOauthToken(code, kakaoOauthInfo);
        KakaoLoginResponse kakaoLoggedInUser = oauthLoginService.processKakaoLogin(kakaoAccessToken.getAccessToken(), kakaoOauthInfo.getLoginUri());

        long loginedUserId = userService.login(kakaoLoggedInUser.getId());
        //Interceptor에서 해당 유저로 조회 시, 필수 정보가 누락되었으면 회원 가입 폼으로 이동시킴.
        session.setAttribute("userId", loginedUserId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 로그인 성공",
                        null));
    }

    @PostMapping("/join")
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> kakaoJoinDev(HttpSession session, @RequestBody JoinRequest joinRequest) {

        long userId = (Long)session.getAttribute("userId");
        userService.join(userId, joinRequest);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 회원가입 성공",
                        null));
    }
}
