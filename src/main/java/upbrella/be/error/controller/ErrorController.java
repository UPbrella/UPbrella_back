package upbrella.be.error.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import upbrella.be.util.CustomResponse;

@RestController
public class ErrorController {
    @RequestMapping(value = "/error", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<CustomResponse> getError() {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "fail",
                        500,
                        "권한이 없는 접근입니다.",
                        null));
    }
}
