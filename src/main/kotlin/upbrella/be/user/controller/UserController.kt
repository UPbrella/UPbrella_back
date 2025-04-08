package upbrella.be.user.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import upbrella.be.rent.service.RentService
import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.request.LoginCodeRequest
import upbrella.be.user.dto.request.UpdateBankAccountRequest
import upbrella.be.user.dto.response.*
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

    @PostMapping("/users/login")
    fun upbrellaLogin(session: HttpSession): ResponseEntity<CustomResponse<Unit>> {
        if (session.getAttribute("kakaoUser") == null) {
            throw NotSocialLoginedException("[ERROR] 카카오 로그인을 먼저 해주세요.")
        }

        val kakaoUser = session.getAttribute("kakaoUser") as KakaoLoginResponse
        val loggedInUser = userService.login(kakaoUser.id!!)

        session.removeAttribute("kakaoUser")
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
        val kakaoUser = session.getAttribute("kakaoUser") as KakaoLoginResponse?

        if (session.getAttribute("user") != null) {
            throw LoginedMemberException("[ERROR] 이미 로그인된 상태입니다.")
        }
        if (kakaoUser == null) {
            throw NotSocialLoginedException("[ERROR] 카카오 로그인을 먼저 해주세요.")
        }

        val loggedInUser = userService.join(kakaoUser, joinRequest)
        session.removeAttribute("kakaoId")
        session.setAttribute("user", loggedInUser)

        log.info("UNU 회원가입 성공")
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "카카오 회원가입 성공",
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
