package upbrella.be.error.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import upbrella.be.util.CustomResponse;

@RestController
public class ErrorController {

    @RequestMapping(value = "/api/error",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PATCH})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomResponse> getError() {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "fail",
                        HttpStatus.BAD_REQUEST.value(),
                        "권한이 없는 접근입니다.",
                        null));
    }
}
