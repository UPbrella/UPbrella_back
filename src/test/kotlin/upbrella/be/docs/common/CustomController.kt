package upbrella.be.docs.common

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import upbrella.be.util.CustomResponse

@RestController
@RequestMapping("/docs")
class CustomController {

    @GetMapping("/common")
    fun showCommon(): ResponseEntity<CustomResponse<Any>> {
        return ResponseEntity
            .ok()
            .body(
                CustomResponse(
                    "success",
                    200,
                    "요청 성공 메시지",
                    "upbrella"
                )
            )
    }

    @GetMapping("/error")
    fun showError(): ResponseEntity<CustomResponse<Any>> {
        return ResponseEntity
            .badRequest()
            .body(
                CustomResponse(
                    "fail",
                    400,
                    "잘못된 요청입니다",
                    "upbrella"
                )
            )
    }
}
