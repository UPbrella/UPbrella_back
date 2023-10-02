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

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
public class UmbrellaController {

    private final UmbrellaService umbrellaService;

    @GetMapping("/admin/umbrellas")
    public ResponseEntity<CustomResponse<UmbrellaPageResponse>> showAllUmbrellas(Pageable pageable) {

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

    @GetMapping("/admin/umbrellas/{storeId}")
    public ResponseEntity<CustomResponse<UmbrellaPageResponse>> showUmbrellasByStoreId(@PathVariable long storeId, Pageable pageable) {

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

    @PostMapping("/admin/umbrellas")
    public ResponseEntity<CustomResponse> addUmbrella(@RequestBody @Valid UmbrellaCreateRequest umbrellaCreateRequest) {

        umbrellaService.addUmbrella(umbrellaCreateRequest);
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 우산 추가 성공",
                        null));
    }

    @PatchMapping("/admin/umbrellas/{id}")
    public ResponseEntity<CustomResponse> modifyUmbrella(@RequestBody @Valid UmbrellaModifyRequest umbrellaModifyRequest, @PathVariable long id) {

        umbrellaService.modifyUmbrella(id, umbrellaModifyRequest);
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "우산 정보 변경 성공",
                        null));
    }

    @DeleteMapping("/admin/umbrellas/{id}")
    public ResponseEntity<CustomResponse> deleteUmbrella(@PathVariable long id) {

        umbrellaService.deleteUmbrella(id);
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "우산 삭제 성공",
                        null));
    }

    @GetMapping("/admin/umbrellas/statistics")
    public ResponseEntity<CustomResponse<UmbrellaStatisticsResponse>> showAllUmbrellasStatistics() {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "전체 우산 통계 조회 성공",
                        umbrellaService.getUmbrellaAllStatistics()));
    }

    @GetMapping("/admin/umbrellas/statistics/{storeId}")
    public ResponseEntity<CustomResponse<UmbrellaStatisticsResponse>> showUmbrellasStatisticsByStore(@PathVariable long storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "지점 우산 통계 조회 성공",
                        umbrellaService.getUmbrellaStatisticsByStoreId(storeId)));
    }
}
