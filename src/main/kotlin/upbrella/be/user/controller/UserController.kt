package upbrella.be.user.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.view.RedirectView
import upbrella.be.rent.service.RentService
import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.request.LoginCodeRequest
import upbrella.be.user.dto.request.UpdateBankAccountRequest
import upbrella.be.user.dto.response.*
import upbrella.be.user.dto.token.AppleOauthInfo
import upbrella.be.user.dto.token.KakaoOauthInfo
import upbrella.be.user.dto.token.OauthToken
import upbrella.be.user.exception.InvalidLoginCodeException
import upbrella.be.user.exception.LoginedMemberException
import upbrella.be.user.exception.NotSocialLoginedException
import upbrella.be.user.service.BlackListService
import upbrella.be.user.service.OauthLoginService
import upbrella.be.user.service.UserService
import upbrella.be.util.CustomResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@RestController
class UserController(
    private val oauthLoginService: OauthLoginService,
    private val userService: UserService,
    private val kakaoOauthInfo: KakaoOauthInfo,
    private val appleOauthInfo: AppleOauthInfo,
    private val rentService: RentService,
    private val blackListService: BlackListService,
) {
    private val log = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/users/loggedIn")
    fun findUserInfo(httpSession: HttpSession): ResponseEntity<CustomResponse<UserInfoResponse>> {
        val sessionUser = httpSession.getAttribute("user") as SessionUser
        val user = userService.findDecryptedUserById(sessionUser)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "로그인 유저 정보 조회 성공",
                UserInfoResponse.fromUser(user)
            ))
    }

    @GetMapping("/users/loggedIn/umbrella")
    fun findUmbrellaBorrowedByUser(httpSession: HttpSession): ResponseEntity<CustomResponse<UmbrellaBorrowedByUserResponse>> {
        val sessionUser = httpSession.getAttribute("user") as SessionUser

        val umbrellaBorrowedByUserResponse = userService.findUmbrellaBorrowedByUser(sessionUser)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "사용자가 빌린 우산 조회 성공",
                umbrellaBorrowedByUserResponse
            ))
    }

    @PostMapping("/users/oauth/login")
    fun kakaoLogin(session: HttpSession, @RequestBody code: LoginCodeRequest): ResponseEntity<CustomResponse<Unit>> {
        val kakaoAccessToken: OauthToken

        try {
            // todo : null 체크
            kakaoAccessToken = oauthLoginService.getOauthToken(code.code, kakaoOauthInfo)!!
        } catch (e: HttpClientErrorException) {
            throw InvalidLoginCodeException("[ERROR] 로그인 코드가 유효하지 않습니다.")
        }

        val kakaoLoggedInUser = oauthLoginService.processKakaoLogin(kakaoAccessToken.accessToken, kakaoOauthInfo.loginUri)
        session.setAttribute("kakaoUser", kakaoLoggedInUser)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "카카오 로그인 성공",
                null
            ))
    }

    @PostMapping("/auth/apple")
    fun appleLoginCallback(
        session: HttpSession,
        @RequestParam code: String,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = false) id_token: String?,
        @RequestParam(required = false) user: String?
    ): RedirectView {
        log.info("Apple login callback received - code: ${code.take(10)}...")

        val appleOauthToken: OauthToken

        try {
            appleOauthToken = oauthLoginService.getOauthToken(code, appleOauthInfo)!!
        } catch (e: HttpClientErrorException) {
            log.error("Apple login failed", e)
            return RedirectView("https://upbrella.co.kr/login?error=apple_login_failed")
        } catch (e: Exception) {
            log.error("Unexpected error during Apple login", e)
            return RedirectView("https://upbrella.co.kr/login?error=server_error")
        }

        // id_token이 없으면 에러
        if (appleOauthToken.idToken.isNullOrEmpty()) {
            log.error("Apple ID token is null or empty")
            return RedirectView("https://upbrella.co.kr/login?error=no_id_token")
        }

        try {
            val appleLoggedInUser = oauthLoginService.processAppleLogin(appleOauthToken.idToken!!)
            session.setAttribute("appleUser", appleLoggedInUser)

            log.info("Apple social login success - redirecting to frontend")
            return RedirectView("https://upbrella.co.kr/login?apple=success")
        } catch (e: Exception) {
            log.error("Apple ID token validation failed", e)
            return RedirectView("https://upbrella.co.kr/login?error=token_validation_failed")
        }
    }

    @PostMapping("/users/oauth/apple/login")
    fun appleLogin(session: HttpSession, @RequestBody code: LoginCodeRequest): ResponseEntity<CustomResponse<Unit>> {
        val appleOauthToken: OauthToken

        try {
            appleOauthToken = oauthLoginService.getOauthToken(code.code, appleOauthInfo)!!
        } catch (e: HttpClientErrorException) {
            throw InvalidLoginCodeException("[ERROR] 로그인 코드가 유효하지 않습니다.")
        }

        // id_token이 없으면 에러
        if (appleOauthToken.idToken.isNullOrEmpty()) {
            throw InvalidLoginCodeException("[ERROR] Apple ID token을 받지 못했습니다.")
        }

        val appleLoggedInUser = oauthLoginService.processAppleLogin(appleOauthToken.idToken!!)
        session.setAttribute("appleUser", appleLoggedInUser)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "애플 로그인 성공",
                null
            ))
    }

    @PostMapping("/users/login")
    fun upbrellaLogin(session: HttpSession): ResponseEntity<CustomResponse<Unit>> {
        val kakaoUser = session.getAttribute("kakaoUser") as? KakaoLoginResponse
        val appleUser = session.getAttribute("appleUser") as? AppleLoginResponse

        if (kakaoUser == null && appleUser == null) {
            throw NotSocialLoginedException("[ERROR] 소셜 로그인을 먼저 해주세요.")
        }

        val loggedInUser = when {
            kakaoUser != null -> {
                val user = userService.login(kakaoUser.id!!)
                session.removeAttribute("kakaoUser")
                user
            }
            appleUser != null -> {
                val user = userService.loginApple(appleUser.sub!!)
                session.removeAttribute("appleUser")
                user
            }
            else -> throw NotSocialLoginedException("[ERROR] 소셜 로그인을 먼저 해주세요.")
        }

        session.setAttribute("user", loggedInUser)

        log.info("UUL 로그인 성공")
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "업브렐라 로그인 성공",
                null
            ))
    }

    @PostMapping("/users/logout")
    fun upbrellaLogout(session: HttpSession): ResponseEntity<CustomResponse<Unit>> {
        session.invalidate()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "업브렐라 로그아웃 성공",
                null
            ))
    }

    @PostMapping("/users/join")
    fun kakaoJoin(session: HttpSession, @RequestBody @Valid joinRequest: JoinRequest): ResponseEntity<CustomResponse<Unit>> {
        val kakaoUser = session.getAttribute("kakaoUser") as? KakaoLoginResponse
        val appleUser = session.getAttribute("appleUser") as? AppleLoginResponse

        if (session.getAttribute("user") != null) {
            throw LoginedMemberException("[ERROR] 이미 로그인된 상태입니다.")
        }
        if (kakaoUser == null && appleUser == null) {
            throw NotSocialLoginedException("[ERROR] 소셜 로그인을 먼저 해주세요.")
        }

        val loggedInUser = when {
            kakaoUser != null -> {
                val user = userService.join(kakaoUser, joinRequest)
                session.removeAttribute("kakaoUser")
                user
            }
            appleUser != null -> {
                val user = userService.joinApple(appleUser, joinRequest)
                session.removeAttribute("appleUser")
                user
            }
            else -> throw NotSocialLoginedException("[ERROR] 소셜 로그인을 먼저 해주세요.")
        }

        session.setAttribute("user", loggedInUser)

        log.info("UNU 회원가입 성공")
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "소셜 회원가입 성공",
                null
            ))
    }

    @GetMapping("/admin/users")
    fun findUsers(): ResponseEntity<CustomResponse<AllUsersInfoResponse>> {
        val allUsersInfoResponse = userService.findUsers()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "회원 목록 정보 조회 성공",
                allUsersInfoResponse
            ))
    }

    @GetMapping("/users/histories")
    fun readUserHistories(session: HttpSession): ResponseEntity<CustomResponse<AllHistoryResponse>> {
        val sessionUser = session.getAttribute("user") as SessionUser

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "사용자 대여 목록 조회 성공",
                rentService.findAllHistoriesByUser(sessionUser.id)
            ))
    }

    @PatchMapping("/users/bankAccount")
    fun updateUserBankAccount(@Valid @RequestBody updateBankAccountRequest: UpdateBankAccountRequest, session: HttpSession): ResponseEntity<CustomResponse<Unit>> {
        val sessionUser = session.getAttribute("user") as SessionUser

        userService.updateUserBankAccount(sessionUser.id, updateBankAccountRequest)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "사용자 계좌 정보 수정 성공"
            ))
    }

    @DeleteMapping("/users/loggedIn")
    fun deleteUser(session: HttpSession): ResponseEntity<CustomResponse<Unit>> {
        val loginedUser = session.getAttribute("user") as SessionUser

        userService.deleteUser(loginedUser.id)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "사용자 탈퇴 성공"
            ))
    }

    @DeleteMapping("/admin/users/{userId}")
    fun withdrawUser(@PathVariable userId: Long): ResponseEntity<CustomResponse<Unit>> {
        userService.withdrawUser(userId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "사용자 탈퇴 성공"
            ))
    }

    @DeleteMapping("/users/bankAccount")
    fun deleteUserBankAccount(session: HttpSession): ResponseEntity<CustomResponse<Unit>> {
        val sessionUser = session.getAttribute("user") as SessionUser

        userService.deleteUserBankAccount(sessionUser.id)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "사용자 계좌 정보 삭제 성공"
            ))
    }

    @GetMapping("/users/blackList")
    fun findBlackList(): ResponseEntity<CustomResponse<AllBlackListResponse>> {
        val blackListResponse = blackListService.findBlackList()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "블랙리스트 조회 성공",
                blackListResponse
            ))
    }

    @DeleteMapping("/users/blackList/{blackListId}")
    fun deleteBlackList(@PathVariable blackListId: Long): ResponseEntity<CustomResponse<Unit>> {
        blackListService.deleteBlackList(blackListId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "블랙리스트 삭제 성공"
            ))
    }

    @PatchMapping("/admin/users/{userId}")
    fun updateAdminStatus(@PathVariable userId: Long): ResponseEntity<CustomResponse<Unit>> {
        userService.updateAdminStatus(userId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "관리자 권한 변경 성공"
            ))
    }
}
