package upbrella.be.rent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.rent.dto.request.HistoryFilterRequest;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.*;
import upbrella.be.rent.service.ConditionReportService;
import upbrella.be.rent.service.RentService;
import upbrella.be.user.entity.User;
import upbrella.be.user.repository.UserRepository;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rent")
@RequiredArgsConstructor
public class RentController {

    private final ConditionReportService conditionReportService;
    private final RentService rentService;

    // 가짜 유저 사용을 위해 임시로 UserRepository 주입
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CustomResponse> rentUmbrellaByUser(@RequestBody RentUmbrellaByUserRequest rentUmbrellaByUserRequest, HttpSession httpSession) {

        // TODO: 세션을 통해 유저 꺼내기
        // 임시로 가짜 유저 사용
        User userToRent = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 유저 고유번호입니다."));

        rentService.addRental(rentUmbrellaByUserRequest, userToRent);

        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "우산 대여 성공"
                ));
    }

    @PatchMapping
    public ResponseEntity<CustomResponse> returnUmbrellaByUser(@RequestBody ReturnUmbrellaByUserRequest returnUmbrellaByUserRequest, HttpSession httpSession) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "우산 반납 성공"
                ));
    }

    @GetMapping("/histories")
    public ResponseEntity<CustomResponse<RentalHistoriesPageResponse>> findRentalHistory(HistoryFilterRequest historyFilterRequest, HttpSession httpSession) {

        RentalHistoriesPageResponse allHistories = rentService.findAllHistories(historyFilterRequest);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대여 내역 조회 성공",
                        allHistories
                ));
    }

    @GetMapping("/histories/status")
    public ResponseEntity<CustomResponse<ConditionReportPageResponse>> showAllConditionReports(HttpSession httpSession) {

        // TODO: 세션 정보로 관리자 식별

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "상태 신고 내역 조회 성공",
                        ConditionReportPageResponse.builder()
                                .conditionReports(
                                        conditionReportService.findAllConditionReport()
                                ).build()));
    }

    @GetMapping("/histories/improvements")
    public ResponseEntity<CustomResponse<ImprovementReportPageResponse>> findImprovements(HttpSession httpSession) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "개선 요청 내역 조회 성공",
                        ImprovementReportPageResponse.builder()
                                .improvementReports(
                                        List.of(ImprovementReportResponse.builder()
                                                .id(1L)
                                                .umbrellaId(30)
                                                .content("정상적인 시기에 반납하기가 어려울 떈 어떻게 하죠?")
                                                .etc("기타 사항")
                                                .build()
                                        )).build()));
    }
}
