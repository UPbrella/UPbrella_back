package upbrella.be.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upbrella.be.user.dto.response.UmbrellaBorrowedByUserResponse;
import upbrella.be.user.dto.response.UserInfoResponse;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public ResponseEntity<CustomResponse<UserInfoResponse>> findUserInfo(HttpSession httpSession) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "로그인 유저 정보 조회 성공",
                        UserInfoResponse.builder()
                                .name("사용자")
                                .phoneNumber("010-0000-0000")
                                .isAdmin(false)
                                .build()));
    }

    @GetMapping("/umbrella")
    public ResponseEntity<CustomResponse<UmbrellaBorrowedByUserResponse>> findUmbrellaBorrowedByUser(HttpSession httpSession) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "로그인 유저 정보 조회 성공",
                        UmbrellaBorrowedByUserResponse.builder().build()));
    }
}
