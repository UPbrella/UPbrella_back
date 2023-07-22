package upbrella.be.rent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.*;
import upbrella.be.rent.service.ConditionReportService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rent")
public class RentController {

    private final ConditionReportService conditionReportService;

    @PostMapping
    public ResponseEntity<CustomResponse> rentUmbrellaByUser(@RequestBody RentUmbrellaByUserRequest rentUmbrellaByUserRequest, HttpSession httpSession) {

        // TODO: 예외 처리



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
    public ResponseEntity<CustomResponse<RentalHistoriesPageResponse>> findRentalHistory(HttpSession httpSession) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대여 내역 조회 성공",
                        RentalHistoriesPageResponse.builder()
                                .rentalHistoryResponsePage(
                                        List.of(RentalHistoryResponse.builder()
                                                .id(1L)
                                                .name("사용자")
                                                .phoneNumber("010-1234-5678")
                                                .rentStoreName("대여점 이름")
                                                .rentAt(LocalDateTime.of(2023, 7, 18, 0, 0, 0))
                                                .elapsedDay(3)
                                                .umbrellaId(30)
                                                .returnStoreName("반납점 이름")
                                                .returnAt(LocalDateTime.now())
                                                .totalRentalDay(5)
                                                .refundCompleted(true)
                                                .build()
                                        )).build()));
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
