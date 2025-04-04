package upbrella.be.error.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import upbrella.be.util.CustomResponse

@RestController
class ErrorController {

    @RequestMapping(
        value = ["/api/error"],
        method = [RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PATCH]
    )
    fun getError(): ResponseEntity<CustomResponse<Any?>> {
        return ResponseEntity
            .badRequest()
            .body(
                CustomResponse(
                    status = "unauthorized",
                    code = HttpStatus.UNAUTHORIZED.value(),
                    message = "세션이 만료되었거나, 권한이 없는 접근입니다."
                )
            )
    }
}