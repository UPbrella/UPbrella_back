package upbrella.be.umbrella.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaPageResponse;
import upbrella.be.umbrella.service.UmbrellaService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping("/umbrellas")
public class UmbrellaController {

    private final UmbrellaService umbrellaService;

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
                                        umbrellaService.findAllUmbrellas()
                ).build()));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<CustomResponse<UmbrellaPageResponse>> showUmbrellasByStoreId(HttpSession httpSession, @PathVariable long storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "지점 우산 현황 조회 성공",
                        UmbrellaPageResponse.builder()
                                .umbrellaResponsePage(
                                        umbrellaService.findUmbrellasByStoreId(storeId)
                                ).build()));
    }

    @PostMapping
    public ResponseEntity<CustomResponse> addUmbrella(HttpSession httpSession, @RequestBody UmbrellaRequest umbrellaRequest) {

        umbrellaService.addUmbrella(umbrellaRequest);
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 우산 추가 성공",
                        null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomResponse> modifyUmbrella(HttpSession httpSession, @RequestBody UmbrellaRequest umbrellaRequest, @PathVariable long id) {

        umbrellaService.modifyUmbrella(id, umbrellaRequest);
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
}