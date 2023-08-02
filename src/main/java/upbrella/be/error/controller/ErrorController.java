package upbrella.be.error.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import upbrella.be.util.CustomResponse;

@RestController
public class ErrorController {

    @GetMapping(value = "/api/error")
    //@ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomResponse> getError() {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "fail",
                        200,
                        "권한이 없는 접근입니다.",
                        null));
    }
}
