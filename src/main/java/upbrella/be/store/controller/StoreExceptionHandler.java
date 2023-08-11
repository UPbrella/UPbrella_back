package upbrella.be.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import upbrella.be.store.exception.DeletedStoreDetailException;
import upbrella.be.store.exception.NonExistingStoreDetailException;
import upbrella.be.store.exception.NonExistingStoreMetaException;
import upbrella.be.util.CustomErrorResponse;

@RestControllerAdvice
public class StoreExceptionHandler {

    @ExceptionHandler(NonExistingStoreDetailException.class)
    public ResponseEntity<CustomErrorResponse> nonExistingStoreDetail(NonExistingStoreDetailException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        "존재하지 않는 협업지점 상세 고유번호입니다."));
    }

    @ExceptionHandler(NonExistingStoreMetaException.class)
    public ResponseEntity<CustomErrorResponse> nonExistingStoreDetail(NonExistingStoreMetaException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        "존재하지 않는 협업지점 고유번호입니다."));
    }

    @ExceptionHandler(DeletedStoreDetailException.class)
    public ResponseEntity<CustomErrorResponse> deletedStoreDetail(DeletedStoreDetailException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        "삭제된 협업 지점 고유번호입니다."));
    }
}
