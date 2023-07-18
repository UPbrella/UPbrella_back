package upbrella.be.rent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.RentHistoryResponse;
import upbrella.be.umbrella.dto.response.UmbrellaPageResponse;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
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
    public ResponseEntity<CustomResponse<RentHistoryResponse>> findRentalHistory(HttpSession httpSession) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대여 내역 조회 성공",
                        RentHistoryResponse.builder()
                                .id(1L)
                                .name("사용자")
                                .phoneNumber("010-1234-5678")
                                .rentStoreName("대여점 이름")
                                .rentAt(LocalDateTime.now())
                                .elapsedDay(3)
                                .umbrellaId(30)
                                .returnStoreName("반납점 이름")
                                .returnAt(LocalDateTime.now())
                                .totalRentalDay(5)
                                .refundCompleted(true)
                                .build()
                ));
    }
}
