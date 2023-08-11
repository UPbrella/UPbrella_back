package upbrella.be.umbrella.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest;
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest;
import upbrella.be.umbrella.dto.response.UmbrellaStatisticsResponse;
import upbrella.be.umbrella.dto.response.UmbrellaPageResponse;
import upbrella.be.umbrella.service.UmbrellaService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/umbrellas")
@RequiredArgsConstructor
public class UmbrellaController {

    private final UmbrellaService umbrellaService;

    @GetMapping
    public ResponseEntity<CustomResponse<UmbrellaPageResponse>> showAllUmbrellas(Pageable pageable, HttpSession httpSession) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "전체 우산 현황 조회 성공",
                        UmbrellaPageResponse.builder()
                                .umbrellaResponsePage(
                                        umbrellaService.findAllUmbrellas(pageable)
                                ).build()));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<CustomResponse<UmbrellaPageResponse>> showUmbrellasByStoreId(@PathVariable long storeId, Pageable pageable, HttpSession httpSession) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "지점 우산 현황 조회 성공",
                        UmbrellaPageResponse.builder()
                                .umbrellaResponsePage(
                                        umbrellaService.findUmbrellasByStoreId(storeId, pageable)
                                ).build()));
    }

    @PostMapping
    public ResponseEntity<CustomResponse> addUmbrella(HttpSession httpSession, @RequestBody UmbrellaCreateRequest umbrellaCreateRequest) {

        umbrellaService.addUmbrella(umbrellaCreateRequest);
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 우산 추가 성공",
                        null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomResponse> modifyUmbrella(HttpSession httpSession, @RequestBody UmbrellaModifyRequest umbrellaModifyRequest, @PathVariable long id) {

        umbrellaService.modifyUmbrella(id, umbrellaModifyRequest);
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "우산 정보 변경 성공",
                        null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteUmbrella(HttpSession httpSession, @PathVariable long id) {

        umbrellaService.deleteUmbrella(id);
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "우산 삭제 성공",
                        null));
    }

    @GetMapping("/statistics")
    public ResponseEntity<CustomResponse<UmbrellaStatisticsResponse>> showAllUmbrellasStatistics(HttpSession httpSession) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "전체 우산 통계 조회 성공",
                        umbrellaService.getUmbrellaAllStatistics()));
    }

    @GetMapping("/statistics/{storeId}")
    public ResponseEntity<CustomResponse<UmbrellaStatisticsResponse>> showUmbrellasStatisticsByStore(HttpSession httpSession, @PathVariable long storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "지점 우산 통계 조회 성공",
                        umbrellaService.getUmbrellaStatisticsByStoreId(storeId)));
    }
}
