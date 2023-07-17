package upbrella.be.umbrella.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaPageResponse;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/umbrellas")
public class UmbrellaController {

    @GetMapping
    public ResponseEntity<CustomResponse<UmbrellaPageResponse>> showAllUmbrellas(HttpSession httpSession) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "전체 우산 현황 조회 성공",
                        UmbrellaPageResponse.builder()
                                .umbrellaResponsePage(
                                        List.of(UmbrellaResponse.builder()
                                                .id(1)
                                                .storeMetaId(2)
                                                .umbrellaId(30)
                                                .rentable(true)
                                                .deleted(false)
                                                .build())
                ).build()));
    }

    @PostMapping
    public ResponseEntity<CustomResponse> addUmbrella(HttpSession httpSession, @RequestBody UmbrellaRequest umbrellaRequest) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 우산 추가 성공",
                        null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse> modifyUmbrella(HttpSession httpSession, @RequestBody UmbrellaRequest umbrellaRequest, @PathVariable int id) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "우산 정보 변경 성공",
                        null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteUmbrella(HttpSession httpSession, @PathVariable int id) {
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "우산 삭제 성공",
                        null));
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
                                        List.of(UmbrellaResponse.builder()
                                                .id(1)
                                                .storeMetaId(2)
                                                .umbrellaId(30)
                                                .rentable(true)
                                                .deleted(false)
                                                .build())
                                ).build()));
    }
}