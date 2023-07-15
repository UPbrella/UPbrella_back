package upbrella.be.umbrella.controller;


import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaPageResponse;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.user.dto.response.UmbrellaBorrowedByUserResponse;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/umbrellas")
public class UmbrellaController {

    @GetMapping
    public ResponseEntity<CustomResponse<UmbrellaPageResponse>> showAllUmbrellas(HttpSession httpSession, Pageable pageable) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "전체 우산 현황 조회 성공",
                        UmbrellaPageResponse.builder()
                                .umbrellaResponsePage(
                                        new PageImpl<>(List.of(UmbrellaResponse.builder()
                                                .id(1)
                                                .storeMetaId(2)
                                                .umbrellaId(30)
                                                .rentable(true)
                                                .build()))
                ).build()));
    }

    @PostMapping
    public ResponseEntity<CustomResponse> addUmbrella(HttpSession httpSession, UmbrellaRequest umbrellaRequest) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 우산 추가 성공"));
    }

    @PatchMapping("/{umbrellaId}")
    public ResponseEntity<CustomResponse> modifyUmbrella(HttpSession httpSession, UmbrellaRequest umbrellaRequest, @PathVariable int umbrellaId) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "우산 정보 변경 성공"));
    }

    @DeleteMapping("/{umbrellaId}")
    public ResponseEntity<CustomResponse> deleteUmbrella(HttpSession httpSession, UmbrellaRequest umbrellaRequest, @PathVariable int umbrellaId) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "우산 정보 변경 성공",
                        UmbrellaBorrowedByUserResponse.builder().build()));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<CustomResponse<UmbrellaPageResponse>> showUmbrellasByStoreId(HttpSession httpSession, @PathVariable int storeId) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "지점 우산 현황 조회 성공",
                        UmbrellaPageResponse.builder()
                                .umbrellaResponsePage(
                                        new PageImpl<>(List.of(UmbrellaResponse.builder()
                                                .id(1)
                                                .storeMetaId(2)
                                                .umbrellaId(30)
                                                .rentable(true)
                                                .build()))
                                ).build()));
    }

    @GetMapping("/status")
    public ResponseEntity<CustomResponse> showUmbrellaStatusReport(HttpSession httpSession) {
        return ResponseEntity
                .ok()
                .body(null);
    }

    @GetMapping("/improvements")
    public ResponseEntity<CustomResponse> showUmbrellaImprovements(HttpSession httpSession) {
        return ResponseEntity
                .ok()
                .body(null);
    }
}