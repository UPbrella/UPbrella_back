package upbrella.be.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upbrella.be.login.dto.response.LoggedInUserResponse;
import upbrella.be.util.CustomResponse;

@RestController
@RequestMapping("/oauth")
public class LoginController {

    @GetMapping("/kakao")
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> kakaoLogin() {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 로그인 성공",
                        LoggedInUserResponse.builder()
                                .id(1L)
                                .socialId(1L)
                                .name("카카오 사용자")
                                .phoneNumber("010-0000-0000")
                                .adminStatus(false)
                                .build()));
    }

    @GetMapping("/naver")
    public ResponseEntity<CustomResponse<LoggedInUserResponse>> naverLogin() {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "네이버 로그인 성공",
                        LoggedInUserResponse.builder()
                                .id(1L)
                                .socialId(1L)
                                .name("네이버 사용자")
                                .phoneNumber("010-0000-0000")
                                .adminStatus(false)
                                .build()));
    }
}
