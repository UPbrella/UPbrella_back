package upbrella.be.rent.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import upbrella.be.rent.exception.*
import upbrella.be.umbrella.exception.MissingUmbrellaException
import upbrella.be.util.CustomErrorResponse

@RestControllerAdvice
class RentExceptionHandler {

    @ExceptionHandler(NonExistingUmbrellaForRentException::class)
    fun nonExistingUmbrellaForRent(e: NonExistingUmbrellaForRentException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                e.message!!
            ))
    }

    @ExceptionHandler(NonExistingHistoryException::class)
    fun nonExistingHistory(e: NonExistingHistoryException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                e.message!!
            ))
    }

    @ExceptionHandler(ExistingUmbrellaForRentException::class)
    fun existingUmbrellaForRent(e: ExistingUmbrellaForRentException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                e.message!!
            ))
    }

    @ExceptionHandler(NotRefundedException::class)
    fun notRefundedException(e: NotRefundedException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                e.message!!
            ))
    }

    @ExceptionHandler(NotAvailableUmbrellaException::class)
    fun notAvailableUmbrellaException(e: NotAvailableUmbrellaException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                e.message!!
            ))
    }

    @ExceptionHandler(LockerCodeAlreadyIssuedException::class)
    fun lockerCodeAlreadyIssuedException(e: LockerCodeAlreadyIssuedException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .status(429)
            .body(CustomErrorResponse(
                "Too Many Requests",
                429,
                e.message!!
            ))
    }

    @ExceptionHandler(NoSignatureException::class)
    fun noSignatureException(e: NoSignatureException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .status(400)
            .body(CustomErrorResponse(
                "Bad Request",
                400,
                e.message!!
            ))
    }

    @ExceptionHandler(LockerSignatureErrorException::class)
    fun lockerSignatureException(e: LockerSignatureErrorException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .status(403)
            .body(CustomErrorResponse(
                "Forbidden",
                403,
                e.message!!
            ))
    }

    @ExceptionHandler(UmbrellaStoreMissMatchException::class)
    fun umbrellaStoreMissMatchException(e: UmbrellaStoreMissMatchException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                e.message!!
            ))
    }

    @ExceptionHandler(MissingUmbrellaException::class)
    fun missingUmbrellaException(e: MissingUmbrellaException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                e.message!!
            ))
    }

    @ExceptionHandler(CannotBeRentedException::class)
    fun cannotBeRentedException(e: CannotBeRentedException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                e.message!!
            ))
    }
}
