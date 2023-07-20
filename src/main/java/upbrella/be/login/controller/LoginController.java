package upbrella.be.login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.login.dto.request.NaverLoginCodeRequest;
import upbrella.be.login.dto.response.LoggedInUser;
import upbrella.be.login.dto.response.LoggedInUserResponse;
import upbrella.be.login.dto.token.NaverToken;
import upbrella.be.login.service.OauthLoginService;
import upbrella.be.user.service.UserService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class LoginController {

    private final OauthLoginService oauthLoginService;
    private final UserService userService;

    @PostMapping("/kakao")
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> kakaoLogin(HttpSession session, @RequestBody String code) {

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
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> naverLogin(HttpSession session, @RequestBody NaverLoginCodeRequest request) {

        NaverToken naverToken = oauthLoginService.getAccessToken(request.getCode(), request.getState());
        LoggedInUser loggedInUser = oauthLoginService.processLogin(naverToken.getAccessToken());
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

    // 로컬 be 개발용
    @GetMapping("/naver")
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> naverLoginDev(HttpSession session, @RequestBody NaverLoginCodeRequest request) {

        NaverToken naverToken = oauthLoginService.getAccessToken(request.getCode(), request.getState());
        LoggedInUser loggedInUser = oauthLoginService.processLogin(naverToken.getAccessToken());
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
