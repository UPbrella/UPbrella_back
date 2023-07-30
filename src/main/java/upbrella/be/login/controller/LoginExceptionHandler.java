package upbrella.be.login.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import upbrella.be.login.exception.ExistingMemberException;

@RestControllerAdvice
public class LoginExceptionHandler {

    @ExceptionHandler({ExistingMemberException.class})
    public String handle(Exception ex) {
        return "Exception Handle!!!";
    }
}