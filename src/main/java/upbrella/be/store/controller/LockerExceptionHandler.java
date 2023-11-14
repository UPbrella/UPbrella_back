package upbrella.be.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import upbrella.be.util.CustomErrorResponse;

@RestControllerAdvice
public class LockerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> nonExistingStoreDetail(IllegalArgumentException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "bad request",
                        400,
                        ex.getMessage()));
    }
}
