package upbrella.be.user.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import upbrella.be.user.exception.*
import upbrella.be.util.CustomErrorResponse
import upbrella.be.util.CustomResponse

@RestControllerAdvice
class UserExceptionHandler {

    @ExceptionHandler(ExistingMemberException::class)
    fun existingMember(ex: ExistingMemberException): ResponseEntity<CustomResponse<*>> {
        return ResponseEntity
            .badRequest()
            .body(CustomResponse<Any?>(
                "fail",
                400,
                "이미 존재하는 회원입니다."
            ))
    }

    @ExceptionHandler(NonExistingMemberException::class)
    fun nonExistingMember(ex: NonExistingMemberException): ResponseEntity<CustomResponse<*>> {
        return ResponseEntity
            .badRequest()
            .body(CustomResponse<Any?>(
                "fail",
                400,
                "존재하지 않는 회원입니다."
            ))
    }

    @ExceptionHandler(InvalidLoginCodeException::class)
    fun invalidLoginCode(ex: InvalidLoginCodeException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                "유효하지 않은 로그인 코드입니다."
            ))
    }

    @ExceptionHandler(LoginedMemberException::class)
    fun loginedMember(ex: LoginedMemberException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                "이미 로그인한 유저입니다."
            ))
    }

    @ExceptionHandler(NotSocialLoginedException::class)
    fun notSocialLogined(ex: NotSocialLoginedException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                "소셜 로그인을 먼저 진행해주세요."
            ))
    }

    @ExceptionHandler(NotLoginException::class)
    fun notLogin(ex: NotLoginException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                "로그인이 필요합니다."
            ))
    }

    @ExceptionHandler(BlackListUserException::class)
    fun blackListUser(ex: BlackListUserException): ResponseEntity<CustomErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(CustomErrorResponse(
                "fail",
                400,
                "정지된 회원입니다. 정지된 회원은 이용이 불가능합니다."
            ))
    }
}
