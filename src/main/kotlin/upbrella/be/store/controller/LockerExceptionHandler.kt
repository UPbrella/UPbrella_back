package upbrella.be.store.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import upbrella.be.util.CustomErrorResponse

@RestControllerAdvice
class LockerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun nonExistingStoreDetail(ex: IllegalArgumentException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "bad request",
                400,
                ex.message!!
            ))
    }
}
