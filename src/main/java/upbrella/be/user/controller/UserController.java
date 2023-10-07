package upbrella.be.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import upbrella.be.rent.service.RentService;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.request.LoginCodeRequest;
import upbrella.be.user.dto.request.UpdateBankAccountRequest;
import upbrella.be.user.dto.response.*;
import upbrella.be.user.dto.token.KakaoOauthInfo;
import upbrella.be.user.dto.token.OauthToken;
import upbrella.be.user.entity.User;
import upbrella.be.user.exception.InvalidLoginCodeException;
import upbrella.be.user.exception.LoginedMemberException;
import upbrella.be.user.exception.NotSocialLoginedException;
import upbrella.be.user.service.OauthLoginService;
import upbrella.be.user.service.UserService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final OauthLoginService oauthLoginService;
    private final UserService userService;
    private final KakaoOauthInfo kakaoOauthInfo;
    private final RentService rentService;

    @GetMapping("/users/loggedIn")
    public ResponseEntity<CustomResponse<UserInfoResponse>> findUserInfo(HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        User user = userService.findDecryptedUserById(sessionUser);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "로그인 유저 정보 조회 성공",
                        UserInfoResponse.fromUser(user)));
    }

    @GetMapping("/users/loggedIn/umbrella")
    public ResponseEntity<CustomResponse<UmbrellaBorrowedByUserResponse>> findUmbrellaBorrowedByUser(HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        UmbrellaBorrowedByUserResponse umbrellaBorrowedByUserResponse = userService.findUmbrellaBorrowedByUser(sessionUser);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자가 빌린 우산 조회 성공",
                        umbrellaBorrowedByUserResponse
                ));
    }

    @PostMapping("/users/oauth/login")
    public ResponseEntity<CustomResponse> kakaoLogin(HttpSession session, @RequestBody LoginCodeRequest code) {

        OauthToken kakaoAccessToken;

        try {
            kakaoAccessToken = oauthLoginService.getOauthToken(code.getCode(), kakaoOauthInfo);
        } catch (HttpClientErrorException e) {
            throw new InvalidLoginCodeException("[ERROR] 로그인 코드가 유효하지 않습니다.");
        }

        KakaoLoginResponse kakaoLoggedInUser = oauthLoginService.processKakaoLogin(kakaoAccessToken.getAccessToken(), kakaoOauthInfo.getLoginUri());
        session.setAttribute("kakaoUser", kakaoLoggedInUser);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 로그인 성공",
                        null));
    }

    @PostMapping("/users/login")
    public ResponseEntity<CustomResponse> upbrellaLogin(HttpSession session) {

        if (session.getAttribute("kakaoUser") == null) {
            throw new NotSocialLoginedException("[ERROR] 카카오 로그인을 먼저 해주세요.");
        }

        KakaoLoginResponse kakaoUser = (KakaoLoginResponse) session.getAttribute("kakaoUser");
        SessionUser loggedInUser = userService.login(kakaoUser.getId());

        session.removeAttribute("kakaoUser");
        session.setAttribute("user", loggedInUser);

        log.info("UUL 로그인 성공");
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "업브렐라 로그인 성공",
                        null));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<CustomResponse> upbrellaLogout(HttpSession session) {

        session.invalidate();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "업브렐라 로그아웃 성공",
                        null));
    }

    @PostMapping("/users/join")
    public ResponseEntity<CustomResponse> kakaoJoin(HttpSession session, @RequestBody @Valid JoinRequest joinRequest) {

        KakaoLoginResponse kakaoUser = (KakaoLoginResponse) session.getAttribute("kakaoUser");

        if (session.getAttribute("user") != null) {
            throw new LoginedMemberException("[ERROR] 이미 로그인된 상태입니다.");
        }
        if (kakaoUser == null) {
            throw new NotSocialLoginedException("[ERROR] 카카오 로그인을 먼저 해주세요.");
        }

        SessionUser loggedInUser = userService.join(kakaoUser, joinRequest);
        session.removeAttribute("kakaoId");
        session.setAttribute("user", loggedInUser);

        log.info("UNU 회원가입 성공");
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 회원가입 성공",
                        null));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<CustomResponse<AllUsersInfoResponse>> findUsers() {

        AllUsersInfoResponse allUsersInfoResponse = userService.findUsers();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "회원 목록 정보 조회 성공",
                        allUsersInfoResponse));
    }

    @GetMapping("/users/histories")
    public ResponseEntity<CustomResponse<AllHistoryResponse>> readUserHistories(HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("user");

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자 대여 목록 조회 성공",
                        rentService.findAllHistoriesByUser(sessionUser.getId())
                ));
    }

    @PatchMapping("/users/bankAccount")
    public ResponseEntity<CustomResponse> updateUserBankAccount(@Valid @RequestBody UpdateBankAccountRequest updateBankAccountRequest, HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("user");

        userService.updateUserBankAccount(sessionUser.getId(), updateBankAccountRequest);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자 계좌 정보 수정 성공"
                ));
    }

    @DeleteMapping("/users/loggedIn")
    public ResponseEntity<CustomResponse> deleteUser(HttpSession session) {

        SessionUser loginedUser = (SessionUser) session.getAttribute("user");

        userService.deleteUser(loginedUser.getId());

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자 탈퇴 성공"
                ));
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<CustomResponse> withdrawUser(@PathVariable long userId) {

        userService.withdrawUser(userId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자 탈퇴 성공"
                ));

    }

    @DeleteMapping("/users/bankAccount")
    public ResponseEntity<CustomResponse> deleteUserBankAccount(HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("user");

        userService.deleteUserBankAccount(sessionUser.getId());

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자 계좌 정보 삭제 성공"
                ));
    }

    @GetMapping("/users/blackList")
    public ResponseEntity<CustomResponse<AllBlackListResponse>> findBlackList() {

        AllBlackListResponse blackListResponse = userService.findBlackList();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "블랙리스트 조회 성공",
                        blackListResponse
                ));
    }

    @DeleteMapping("/users/blackList/{blackListId}")
    public ResponseEntity<CustomResponse> deleteBlackList(@PathVariable long blackListId) {

        userService.deleteBlackList(blackListId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "블랙리스트 삭제 성공"
                ));
    }

    @PatchMapping("/admin/users/{userId}")
    public ResponseEntity<CustomResponse> updateAdminStatus(@PathVariable long userId) {

        userService.updateAdminStatus(userId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "관리자 권한 변경 성공"
                ));
    }
}
