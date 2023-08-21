package upbrella.be.umbrella.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import upbrella.be.umbrella.exception.ExistingUmbrellaUuidException;
import upbrella.be.umbrella.exception.NonExistingUmbrellaException;
import upbrella.be.util.CustomErrorResponse;

@RestControllerAdvice
public class UmbrellaExceptionHandler {

    @ExceptionHandler(ExistingUmbrellaUuidException.class)
    public ResponseEntity<CustomErrorResponse> existingUmbrella(ExistingUmbrellaUuidException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        "이미 존재하는 우산 관리 번호입니다."));
    }

    @ExceptionHandler(NonExistingUmbrellaException.class)
    public ResponseEntity<CustomErrorResponse> nonExistingUmbrella(NonExistingUmbrellaException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        "존재하지 않는 우산 고유 번호입니다."));
    }
}
