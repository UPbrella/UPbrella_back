package upbrella.be.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import upbrella.be.user.exception.ExistingMemberException;
import upbrella.be.util.CustomResponse;

@RestControllerAdvice
public class LoginExceptionHandler {

    @ExceptionHandler({ExistingMemberException.class})
    public ResponseEntity<CustomResponse>  unauthorizedAccess(Exception ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomResponse<>(
                        "fail",
                        400,
                        "이미 존재하는 회원입니다.",
                        null));
    }
}