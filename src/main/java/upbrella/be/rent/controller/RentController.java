package upbrella.be.rent.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.rent.dto.request.HistoryFilterRequest;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.*;
import upbrella.be.rent.service.ConditionReportService;
import upbrella.be.rent.service.ImprovementReportService;
import upbrella.be.rent.service.LockerService;
import upbrella.be.rent.service.RentService;
import upbrella.be.slack.service.SlackAlarmService;
import upbrella.be.user.dto.response.SessionUser;
import upbrella.be.user.entity.User;
import upbrella.be.user.service.UserService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RentController {

    private final ConditionReportService conditionReportService;
    private final ImprovementReportService improvementReportService;
    private final RentService rentService;
    private final UserService userService;
    private final SlackAlarmService slackAlarmService;
    private final LockerService lockerService;

    @GetMapping("/rent/form/{umbrellaId}")
    public ResponseEntity<CustomResponse<RentFormResponse>> findRentForm(@PathVariable long umbrellaId) {

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

    @GetMapping("/return/form/{storeId}")
    public ResponseEntity<CustomResponse<ReturnFormResponse>> findReturnForm(
            @PathVariable long storeId,
            HttpSession httpSession,
            @RequestParam(required = false) String salt,
            @RequestParam(required = false) String signature) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        User userToReturn = userService.findUserById(user.getId());

        ReturnFormResponse returnForm = rentService.findReturnForm(storeId, userToReturn, salt, signature);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "반납 폼 조회 성공",
                        returnForm
                ));
    }

    @PostMapping("/rent")
    public ResponseEntity<CustomResponse<LockerPasswordResponse>> rentUmbrellaByUser(@RequestBody @Valid RentUmbrellaByUserRequest rentUmbrellaByUserRequest, HttpSession httpSession) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        User userToRent = userService.findUserById(user.getId());

        LockerPasswordResponse lockerPasswordResponse = lockerService.findLockerPassword(rentUmbrellaByUserRequest);

        rentService.addRental(rentUmbrellaByUserRequest, userToRent);

        log.info("UBU 우산 대여 성공");

        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "우산 대여 성공",
                        lockerPasswordResponse
                ));
    }


    @PatchMapping("/rent")
    public ResponseEntity<CustomResponse> returnUmbrellaByUser(
            @RequestBody @Valid
            ReturnUmbrellaByUserRequest returnUmbrellaByUserRequest,
            HttpSession httpSession) {

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        User userToReturn = userService.findUserById(user.getId());

        rentService.returnUmbrellaByUser(userToReturn, returnUmbrellaByUserRequest);
        long unrefundedRentCount = rentService.countUnrefundedRent();

        slackAlarmService.notifyReturn(unrefundedRentCount);
        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "우산 반납 성공"
                ));
    }

    @GetMapping("/admin/rent/histories")
    public ResponseEntity<CustomResponse<RentalHistoriesPageResponse>> findRentalHistory
            (@ModelAttribute HistoryFilterRequest filter, Pageable pageable) {

        RentalHistoriesPageResponse histories = rentService.findAllHistories(filter, pageable);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "어드민 대여 내역 조회 성공",
                        histories
                ));
    }

    @GetMapping("/admin/rent/histories/status")
    public ResponseEntity<CustomResponse<ConditionReportPageResponse>> findConditionReports() {

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

    @GetMapping("/admin/rent/histories/improvements")
    public ResponseEntity<CustomResponse<ImprovementReportPageResponse>> findImprovements() {

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

    @PatchMapping("/admin/rent/histories/refund/{historyId}")
    public ResponseEntity<CustomResponse> refundRent(@PathVariable long historyId, HttpSession httpSession) {

        SessionUser loginedUser = (SessionUser) httpSession.getAttribute("user");

        rentService.checkRefund(historyId, loginedUser.getId());
        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "환급 확인 성공"
                ));
    }

    @PatchMapping("/admin/rent/histories/payment/{historyId}")
    public ResponseEntity<CustomResponse> checkPayment(@PathVariable long historyId, HttpSession httpSession) {

        SessionUser loginedUser = (SessionUser) httpSession.getAttribute("user");

        rentService.checkPayment(historyId, loginedUser.getId());
        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "입금 확인 성공"
                ));
    }

    @DeleteMapping("/admin/rent/histories/{historyId}/account")
    public ResponseEntity<CustomResponse> deleteBankAccount(@PathVariable long historyId) {

        rentService.deleteBankAccount(historyId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "대여 기록의 계좌 삭제 성공"
                ));
    }
}
