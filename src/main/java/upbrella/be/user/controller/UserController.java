package upbrella.be.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.rent.service.RentService;
import upbrella.be.user.dto.request.JoinRequest;
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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final OauthLoginService oauthLoginService;
    private final UserService userService;
    private final KakaoOauthInfo kakaoOauthInfo;
    private final RentRepository rentRepository;
    private final RentService rentService;

    @GetMapping("/loggedIn")
    public ResponseEntity<CustomResponse<UserInfoResponse>> findUserInfo(HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        User user = userService.findUserById(sessionUser.getId());

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "로그인 유저 정보 조회 성공",
                        UserInfoResponse.fromUser(user)));
    }

    @GetMapping("/loggedIn/umbrella")
    public ResponseEntity<CustomResponse<UmbrellaBorrowedByUserResponse>> findUmbrellaBorrowedByUser(HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        History rentalHistory = rentRepository.findByUserAndReturnedAtIsNull(sessionUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 사용자가 빌린 우산이 없습니다."));

        long borrowedUmbrellaUuid = rentalHistory.getUmbrella().getUuid();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자가 빌린 우산 조회 성공",
                        UmbrellaBorrowedByUserResponse.builder()
                                .uuid(borrowedUmbrellaUuid)
                                .build()));
    }

    @GetMapping("/oauth/login")
    public ResponseEntity<CustomResponse> kakaoLogin(HttpSession session, String code) {

        OauthToken kakaoAccessToken;

        try {
            kakaoAccessToken = oauthLoginService.getOauthToken(code, kakaoOauthInfo);
        } catch (HttpClientErrorException e) {
            throw new InvalidLoginCodeException("[ERROR] 로그인 코드가 유효하지 않습니다.");
        }

        KakaoLoginResponse kakaoLoggedInUser = oauthLoginService.processKakaoLogin(kakaoAccessToken.getAccessToken(), kakaoOauthInfo.getLoginUri());
        session.setAttribute("kakaoId", kakaoLoggedInUser.getId());

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 로그인 성공",
                        null));
    }

    @GetMapping("/login")
    public ResponseEntity<CustomResponse> upbrellaLogin(HttpSession session) {

        Long kakaoId = (Long) session.getAttribute("kakaoId");
        SessionUser loginedUserId = userService.login(kakaoId);

        session.removeAttribute("kakaoId");
        session.setAttribute("user", loginedUserId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "업브렐라 로그인 성공",
                        null));
    }

    @PostMapping("/join")
    public ResponseEntity<CustomResponse> kakaoJoin(HttpSession session, @RequestBody JoinRequest joinRequest) {

        Long kakaoId = (Long) session.getAttribute("kakaoId");

        if (session.getAttribute("user") != null) {
            throw new LoginedMemberException("[ERROR] 이미 로그인된 상태입니다.");
        }
        if (kakaoId == null) {
            throw new NotSocialLoginedException("[ERROR] 카카오 로그인을 먼저 해주세요.");
        }

        SessionUser loggedInUser = userService.join(kakaoId, joinRequest);

        session.removeAttribute("kakaoId");
        session.setAttribute("user", loggedInUser);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 회원가입 성공",
                        null));
    }

    @GetMapping
    public ResponseEntity<CustomResponse<AllUsersInfoResponse>> findUsers(HttpSession httpSession) {

        AllUsersInfoResponse allUsersInfoResponse = userService.findUsers();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "회원 목록 정보 조회 성공",
                        allUsersInfoResponse));
    }

    @GetMapping("/histories")
    public ResponseEntity<CustomResponse> readUserHistories(HttpSession session) {

        long loginedUserId = (long) session.getAttribute("userId");

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자 대여 목록 조회 성공",
                        rentService.findAllHistoriesByUser(loginedUserId)
                ));
    }

    @PatchMapping("/bankAccount")
    public ResponseEntity<CustomResponse> updateUserBankAccount(HttpSession session, @Valid @RequestBody UpdateBankAccountRequest updateBankAccountRequest) {

        long loginedUserId = (long) session.getAttribute("userId");

        userService.updateUserBankAccount(loginedUserId, updateBankAccountRequest);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자 계좌 정보 수정 성공"
                ));
    }

    @DeleteMapping("/loggedIn")
    public ResponseEntity<CustomResponse> deleteUser(HttpSession session) {

        long loginedUserId = (long) session.getAttribute("userId");

        userService.deleteUser(loginedUserId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자 탈퇴 성공"
                ));
    }

    // TODO: 관리자 권한 어디서 처리할것인지
    @DeleteMapping("/{userId}")
    public ResponseEntity<CustomResponse> withdrawUser(HttpSession session, @PathVariable long userId) {

        userService.withdrawUser(userId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "사용자 탈퇴 성공"
                ));

    }
}
