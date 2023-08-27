package upbrella.be.rent.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.rent.dto.request.HistoryFilterRequest;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.ConditionReportPageResponse;
import upbrella.be.rent.dto.response.ImprovementReportPageResponse;
import upbrella.be.rent.dto.response.RentFormResponse;
import upbrella.be.rent.dto.response.RentalHistoriesPageResponse;
import upbrella.be.rent.service.ConditionReportService;
import upbrella.be.rent.service.ImprovementReportService;
import upbrella.be.rent.service.RentService;
import upbrella.be.slack.service.SlackAlarmService;
import upbrella.be.user.entity.User;
import upbrella.be.user.repository.UserRepository;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/rent")
@RequiredArgsConstructor
public class RentController {

    private final ConditionReportService conditionReportService;
    private final ImprovementReportService improvementReportService;
    private final RentService rentService;
    private final SlackAlarmService slackAlarmService;

    // 가짜 유저 사용을 위해 임시로 UserRepository 주입
    private final UserRepository userRepository;

    @GetMapping("/form/{umbrellaId}")
    public ResponseEntity<CustomResponse<RentFormResponse>> findRentForm(@PathVariable long umbrellaId, HttpSession httpSession) {

        RentFormResponse rentForm = rentService.findRentForm(umbrellaId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대여 폼 조회 성공",
                        rentForm
                ));
    }

    @PostMapping
    public ResponseEntity<CustomResponse> rentUmbrellaByUser(@RequestBody RentUmbrellaByUserRequest rentUmbrellaByUserRequest, HttpSession httpSession) {

        // TODO: 세션을 통해 유저 꺼내기
        // 임시로 가짜 유저 사용
        User userToRent = userRepository.findById(70L)
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
    public ResponseEntity<CustomResponse> returnUmbrellaByUser(@RequestBody ReturnUmbrellaByUserRequest returnUmbrellaByUserRequest, HttpSession httpSession) throws JsonProcessingException {

        // TODO: 세션을 통해 유저 꺼내기
        /**
         * 세션 처리 이후 로직
         *
         * User userToReturn = userRepository.findById(87L)
         *                 .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 유저 고유번호입니다."));
         */
        // 임시로 가짜 유저 사용
        User userToReturn = userRepository.findById(70L).get();

        rentService.returnUmbrellaByUser(userToReturn, returnUmbrellaByUserRequest);

        slackAlarmService.notifyReturn(userToReturn, returnUmbrellaByUserRequest);
        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "우산 반납 성공"
                ));
    }

    @GetMapping("/histories")
    public ResponseEntity<CustomResponse<RentalHistoriesPageResponse>> findRentalHistory(HistoryFilterRequest filter, HttpSession httpSession) {

        RentalHistoriesPageResponse histories = rentService.findAllHistories(filter);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대여 내역 조회 성공",
                        histories
                ));
    }

    @GetMapping("/histories/status")
    public ResponseEntity<CustomResponse<ConditionReportPageResponse>> findConditionReports(HttpSession httpSession) {

        // TODO: 세션 정보로 관리자 식별

        ConditionReportPageResponse conditionReports = conditionReportService.findAll();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "상태 신고 내역 조회 성공",
                        conditionReports
                ));
    }

    @GetMapping("/histories/improvements")
    public ResponseEntity<CustomResponse<ImprovementReportPageResponse>> findImprovements(HttpSession httpSession) {

        ImprovementReportPageResponse improvementReports = improvementReportService.findAll();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "개선 요청 내역 조회 성공",
                        improvementReports
                ));
    }

    @PatchMapping("/histories/refund/{historyId}")
    public ResponseEntity<CustomResponse> refundRent(@PathVariable long historyId, HttpSession httpSession) {

        long loginedUserId = (long) httpSession.getAttribute("userId");

        rentService.checkRefund(historyId, loginedUserId);
        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "환급 확인 성공"
                ));
    }

    @PatchMapping("/histories/payment/{historyId}")
    public ResponseEntity<CustomResponse> checkPayment(@PathVariable long historyId, HttpSession httpSession) {

        long loginedUserId = (long) httpSession.getAttribute("userId");

        rentService.checkPayment(historyId, loginedUserId);
        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "입금 확인 성공"
                ));
    }
}
