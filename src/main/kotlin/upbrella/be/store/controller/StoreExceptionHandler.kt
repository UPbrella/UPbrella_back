package upbrella.be.store.controller

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import upbrella.be.store.exception.*
import upbrella.be.util.CustomErrorResponse

@RestControllerAdvice
class StoreExceptionHandler {

    @ExceptionHandler(NonExistingStoreDetailException::class)
    fun nonExistingStoreDetail(ex: NonExistingStoreDetailException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "not found",
                404,
                ex.message!!
            ))
    }

    @ExceptionHandler(DeletedStoreDetailException::class)
    fun deletedStoreDetail(ex: DeletedStoreDetailException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "not found",
                404,
                ex.message!!
            ))
    }

    @ExceptionHandler(NonExistingStoreMetaException::class)
    fun nonExistingStoreMeta(ex: NonExistingStoreMetaException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "not found",
                404,
                ex.message!!
            ))
    }

    @ExceptionHandler(NonExistingStoreImageException::class)
    fun nonExistingStoreImage(ex: NonExistingStoreImageException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "not found",
                404,
                ex.message!!
            ))
    }

    @ExceptionHandler(IncorrectClassificationException::class)
    fun incorrectClassification(ex: IncorrectClassificationException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "bad request",
                400,
                ex.message!!
            ))
    }

    @ExceptionHandler(AssignedClassificationException::class)
    fun assignedClassification(ex: AssignedClassificationException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "bad request",
                400,
                ex.message!!
            ))
    }

    @ExceptionHandler(NonExistingClassificationException::class)
    fun nonExistingClassification(ex: NonExistingClassificationException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "not found",
                404,
                ex.message!!
            ))
    }

    @ExceptionHandler(EssentialImageException::class)
    fun essentialImage(ex: EssentialImageException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "bad request",
                400,
                ex.message!!
            ))
    }

    @ExceptionHandler(NotExistBusinessHourException::class)
    fun notExistBusinessHour(ex: NotExistBusinessHourException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "not found",
                404,
                ex.message!!
            ))
    }

    @ExceptionHandler(FileSizeLimitExceededException::class)
    fun fileSize(ex: FileSizeLimitExceededException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "bad request",
                400,
                ex.message!!
            ))
    }
}
