package upbrella.be.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import upbrella.be.user.exception.*;
import upbrella.be.util.CustomResponse;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(ExistingMemberException.class)
    public ResponseEntity<CustomResponse>  existingMember(ExistingMemberException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomResponse<>(
                        "fail",
                        400,
                        "이미 존재하는 회원입니다.",
                        null));
    }

    @ExceptionHandler(NonExistingMemberException.class)
    public ResponseEntity<CustomResponse>  nonExistingMember(NonExistingMemberException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomResponse<>(
                        "fail",
                        400,
                        "존재하지 않는 회원입니다.",
                        null));
    }

    @ExceptionHandler(InvalidLoginCodeException.class)
    public ResponseEntity<CustomResponse>  invalidLoginCode(InvalidLoginCodeException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomResponse<>(
                        "fail",
                        400,
                        "유효하지 않은 로그인 코드입니다.",
                        null));
    }

    @ExceptionHandler(LoginedMemberException.class)
    public ResponseEntity<CustomResponse>  loginedMember(LoginedMemberException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomResponse<>(
                        "fail",
                        400,
                        "이미 로그인한 유저입니다.",
                        null));
    }

    @ExceptionHandler(NotSocialLoginedException.class)
    public ResponseEntity<CustomResponse>  notSocialLogined(NotSocialLoginedException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomResponse<>(
                        "fail",
                        400,
                        "소셜 로그인을 먼저 진행해주세요.",
                        null));
    }
}