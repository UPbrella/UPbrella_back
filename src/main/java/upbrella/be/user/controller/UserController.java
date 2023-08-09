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
import upbrella.be.user.dto.response.AllUsersInfoResponse;
import upbrella.be.user.dto.response.KakaoLoginResponse;
import upbrella.be.user.dto.response.UmbrellaBorrowedByUserResponse;
import upbrella.be.user.dto.response.UserInfoResponse;
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

        /**
         * TODO: 세션 처리으로 로그인한 유저의 id 가져오기
         *       interceptor에서 userId를 가져올 것인지, user 객체를 가져올 것인지 논의 후 구현
         */

        // session에서 꺼낸 것이라는 의미로 repository로부터 findById를 하지 않고 빌더를 사용해서 만들었음.
        User loggedInUser = User.builder()
                .id(1L)
                .name("사용자")
                .phoneNumber("010-0000-0000")
                .adminStatus(false)
                .build();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "로그인 유저 정보 조회 성공",
                        UserInfoResponse.fromUser(loggedInUser)));
    }

    @GetMapping("/loggedIn/umbrella")
    public ResponseEntity<CustomResponse<UmbrellaBorrowedByUserResponse>> findUmbrellaBorrowedByUser(HttpSession httpSession) {

        /**
         * TODO: 세션 처리으로 로그인한 유저의 id 가져오기
         *       interceptor에서 userId를 가져올 것인지, user 객체를 가져올 것인지 논의 후 구현
         */

        // session에서 꺼낸 것이라는 의미로 repository로부터 findById를 하지 않고 빌더를 사용해서 만들었음.
        User loggedInUser = User.builder()
                .id(72L)
                .name("사용자")
                .phoneNumber("010-1234-5678")
                .adminStatus(false)
                .build();

        History rentalHistory = rentRepository.findByUserAndReturnedAtIsNull(loggedInUser.getId())
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

    @GetMapping("/login")
    public ResponseEntity<CustomResponse> kakaoLogin(HttpSession session, String code) {

        OauthToken kakaoAccessToken;
        try {
            kakaoAccessToken = oauthLoginService.getOauthToken(code, kakaoOauthInfo);
        } catch (HttpClientErrorException e) {
            throw new InvalidLoginCodeException("[ERROR] 로그인 코드가 유효하지 않습니다.");
        }
        session.setAttribute("authToken", kakaoAccessToken);

        KakaoLoginResponse kakaoLoggedInUser = oauthLoginService.processKakaoLogin(kakaoAccessToken.getAccessToken(), kakaoOauthInfo.getLoginUri());
        long loginedUserId = userService.login(kakaoLoggedInUser.getId());
        session.removeAttribute("authToken");

        session.setAttribute("userId", loginedUserId);
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "카카오 로그인 성공",
                        null));
    }

    @PostMapping("/join")
    public ResponseEntity<CustomResponse> kakaoJoin(HttpSession session, @RequestBody JoinRequest joinRequest) {

        OauthToken kakaoAccessToken = (OauthToken) session.getAttribute("authToken");

        if (session.getAttribute("userId") != null) {
            throw new LoginedMemberException("[ERROR] 이미 로그인된 상태입니다.");
        }

        if (kakaoAccessToken == null) {
            throw new NotSocialLoginedException("[ERROR] 로그인을 먼저 해주세요.");
        }

        KakaoLoginResponse kakaoLoggedInUser = oauthLoginService.processKakaoLogin(kakaoAccessToken.getAccessToken(), kakaoOauthInfo.getLoginUri());

        long loginedUserId = userService.join(kakaoLoggedInUser.getId(), joinRequest);

        session.removeAttribute("authToken");
        session.setAttribute("userId", loginedUserId);

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
}
