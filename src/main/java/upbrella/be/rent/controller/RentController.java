package upbrella.be.rent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.RentalHistoriesPageResponse;
import upbrella.be.rent.dto.response.RentalHistoryResponse;
import upbrella.be.rent.dto.response.StatusDeclarationPageResponse;
import upbrella.be.rent.dto.response.StatusDeclarationResponse;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rent")
public class RentController {

    @PostMapping
    public ResponseEntity<CustomResponse> rentUmbrellaByUser(@RequestBody RentUmbrellaByUserRequest rentUmbrellaByUserRequest, HttpSession httpSession) {

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
                                                .returnAt(LocalDateTime.of(2023, 7, 23, 0, 0, 0))
                                                .totalRentalDay(5)
                                                .refundCompleted(true)
                                                .build()
                                        )).build()));
    }

    @GetMapping("/histories/status")
    public ResponseEntity<CustomResponse<StatusDeclarationPageResponse>> findStatusDeclarations(HttpSession httpSession) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "상태 신고 내용 조회 성공",
                        StatusDeclarationPageResponse.builder()
                                .statusDeclarationPage(
                                        List.of(StatusDeclarationResponse.builder()
                                                .id(1L)
                                                .umbrellaId(30)
                                                .content("우산이 망가졌어요")
                                                .etc("기타 사항")
                                                .build()
                                        )).build()));
    }
}
