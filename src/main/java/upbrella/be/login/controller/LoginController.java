package upbrella.be.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.login.dto.request.JoinRequest;
import upbrella.be.login.dto.response.KakaoLoginResponse;
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
    private final KakaoOauthInfo kakaoOauthInfo;

    public LoginController(OauthLoginService oauthLoginService, UserService userService, KakaoOauthInfo kakaoOauthInfo) {
        this.oauthLoginService = oauthLoginService;
        this.userService = userService;
        this.kakaoOauthInfo = kakaoOauthInfo;
    }

    @GetMapping("/login")
    public ResponseEntity<CustomResponse> kakaoLogin(HttpSession session, String code) {

        OauthToken kakaoAccessToken = oauthLoginService.getOauthToken(code, kakaoOauthInfo);
        session.setAttribute("authToken", kakaoAccessToken);

        KakaoLoginResponse kakaoLoggedInUser = oauthLoginService.processKakaoLogin(kakaoAccessToken.getAccessToken(), kakaoOauthInfo.getLoginUri());
        long loginedUserId = userService.login(kakaoLoggedInUser.getId());
        session.removeAttribute("authToken");

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
    public ResponseEntity<CustomResponse> kakaoJoin(HttpSession session, @RequestBody JoinRequest joinRequest) {

        OauthToken kakaoAccessToken = (OauthToken)session.getAttribute("authToken");

        if (session.getAttribute("userId") != null) {
            throw new IllegalArgumentException("[ERROR] 이미 로그인된 상태입니다.");
        }

        if (kakaoAccessToken == null) {
            throw new IllegalArgumentException("[ERROR] 로그인을 먼저 해주세요.");
        }

        KakaoLoginResponse kakaoLoggedInUser = oauthLoginService.processKakaoLogin(kakaoAccessToken.getAccessToken(), kakaoOauthInfo.getLoginUri());

        long loginedUserId = userService.join(kakaoLoggedInUser.getId(), joinRequest);

        session.removeAttribute("authToken");
        session.setAttribute("userId", loginedUserId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 회원가입 성공",
                        null));
    }
}
