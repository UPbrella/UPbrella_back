package upbrella.be.docs.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upbrella.be.util.CustomResponse;

@RestController
@RequestMapping("/docs")
public class CustomController {

    @GetMapping("/common")
    public ResponseEntity<CustomResponse> showCommon() {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "요청 성공 메시지","upbrella"));
    }

    @GetMapping("/error")
    public ResponseEntity<CustomResponse> showError() {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "fail",
                        400,
                        "잘못된 요청입니다.","umbrella"));
    }
}
