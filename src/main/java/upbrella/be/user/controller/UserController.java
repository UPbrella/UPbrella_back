package upbrella.be.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.umbrella.repository.UmbrellaRepository;
import upbrella.be.user.dto.response.UmbrellaBorrowedByUserResponse;
import upbrella.be.user.dto.response.UserInfoResponse;
import upbrella.be.user.entity.User;
import upbrella.be.user.service.UserService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RentRepository rentRepository;

    @GetMapping
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

    @GetMapping("/umbrella")
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
}
