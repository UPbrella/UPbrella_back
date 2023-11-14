package upbrella.be.rent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import upbrella.be.rent.exception.*;
import upbrella.be.umbrella.exception.MissingUmbrellaException;
import upbrella.be.util.CustomErrorResponse;

@RestControllerAdvice
public class RentExceptionHandler {

    @ExceptionHandler(NonExistingUmbrellaForRentException.class)
    public ResponseEntity<CustomErrorResponse> nonExistingUmbrellaForRent(NonExistingUmbrellaForRentException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(NonExistingHistoryException.class)
    public ResponseEntity<CustomErrorResponse> nonExistingHistory(NonExistingHistoryException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(ExistingUmbrellaForRentException.class)
    public ResponseEntity<CustomErrorResponse> existingUmbrellaForRent(ExistingUmbrellaForRentException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(NotRefundedException.class)
    public ResponseEntity<CustomErrorResponse> notRefundedException(NotRefundedException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(NotAvailableUmbrellaException.class)
    public ResponseEntity<CustomErrorResponse> notAvailableUmbrellaException(NotAvailableUmbrellaException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(LockerCodeAlreadyIssuedException.class)
    public ResponseEntity<CustomErrorResponse> lockerCodeAlreadyIssuedException(LockerCodeAlreadyIssuedException e) {

        return ResponseEntity
                .status(429)
                .body(new CustomErrorResponse(
                        "Too Many Requests",
                        429,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(NoSignatureException.class)
    public ResponseEntity<CustomErrorResponse> noSignatureException(NoSignatureException e) {

        return ResponseEntity
                .status(400)
                .body(new CustomErrorResponse(
                        "Bad Request",
                        400,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(LockerSignatureErrorException.class)
    public ResponseEntity<CustomErrorResponse> lockerSignatureException(LockerSignatureErrorException e) {

        return ResponseEntity
                .status(403)
                .body(new CustomErrorResponse(
                        "Forbidden",
                        403,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(UmbrellaStoreMissMatchException.class)
    public ResponseEntity<CustomErrorResponse> umbrellaStoreMissMatchException(UmbrellaStoreMissMatchException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(MissingUmbrellaException.class)
    public ResponseEntity<CustomErrorResponse> missingUmbrellaException(MissingUmbrellaException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(CannotBeRentedException.class)
    public ResponseEntity<CustomErrorResponse> cannotBeRentedException(CannotBeRentedException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        e.getMessage()
                ));
    }
}
