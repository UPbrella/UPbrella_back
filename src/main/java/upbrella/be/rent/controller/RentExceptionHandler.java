package upbrella.be.rent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import upbrella.be.rent.exception.NonExistingUmbrellaForRentException;
import upbrella.be.store.exception.NonExistingClassificationException;
import upbrella.be.util.CustomErrorResponse;

@RestControllerAdvice
public class RentExceptionHandler {

    @ExceptionHandler(NonExistingUmbrellaForRentException.class)
    public ResponseEntity<CustomErrorResponse> nonExistingUmbrellaForRent(NonExistingClassificationException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        "대여 중인 우산이 없습니다."
                ));
    }
}