package upbrella.be.login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.login.dto.response.LoggedInUserResponse;
import upbrella.be.login.dto.token.NaverToken;
import upbrella.be.login.service.OauthLoginService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class LoginController {

    private final OauthLoginService oauthLoginService;

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
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> naverLogin(HttpSession session, @RequestBody String code) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "네이버 로그인 성공",
                        LoggedInUserResponse.builder()
                                .id(1L)
                                .name("네이버 사용자")
                                .phoneNumber("010-0000-0000")
                                .adminStatus(false)
                                .build()));
    }

    @GetMapping("/naver")
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> naverLoginDev(HttpSession session, @RequestParam String code, @RequestParam String state) {

        NaverToken accessToken = oauthLoginService.getAccessToken(code, state);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "네이버 로그인 성공",
                        LoggedInUserResponse.builder()
                                .id(1L)
                                .name("네이버 사용자")
                                .phoneNumber("010-0000-0000")
                                .adminStatus(false)
                                .build()));
    }
}
