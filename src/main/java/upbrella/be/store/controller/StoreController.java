package upbrella.be.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upbrella.be.store.dto.request.CreateClassificationRequest;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.CreateSubClassificationRequest;
import upbrella.be.store.dto.request.UpdateStoreRequest;
import upbrella.be.store.dto.response.*;
import upbrella.be.store.service.ClassificationService;
import upbrella.be.store.service.StoreDetailService;
import upbrella.be.store.service.StoreImageService;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.util.CustomResponse;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreImageService storeImageService;
    private final StoreMetaService storeMetaService;
    private final ClassificationService classificationService;
    private final StoreDetailService storeDetailService;

    @GetMapping("/stores/{storeId}")
    public ResponseEntity<CustomResponse<StoreFindByIdResponse>> findStoreById(@PathVariable long storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "가게 조회 성공",
                        storeDetailService.findStoreDetailByStoreId(storeId)));
    }

    @GetMapping("/stores/classification/{classificationId}")
    public ResponseEntity<CustomResponse<AllCurrentLocationStoreResponse>> findCurrentLocationStore(@PathVariable long classificationId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대분류 기준 가게 조회 성공",
                        storeMetaService.findAllStoresByClassification(classificationId, LocalDateTime.now())));
    }

    @GetMapping("/stores/location/{umbrellaId}")
    public ResponseEntity<CustomResponse<CurrentUmbrellaStoreResponse>> findCurrentUmbrellaStore(@PathVariable long umbrellaId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "현재 우산 가게 조회 성공",
                        storeMetaService.findCurrentStoreIdByUmbrella(umbrellaId)));
    }


    @GetMapping("/admin/stores")
    public ResponseEntity<CustomResponse<AllStoreResponse>> findAllStores() {

        List<SingleStoreResponse> allStores = storeDetailService.findAllStores();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "어드민 가게 전체 조회 성공",
                        AllStoreResponse.builder()
                                .stores(allStores)
                                .build()
                ));
    }

    @PostMapping("/admin/stores")
    public ResponseEntity<CustomResponse> createStore(@RequestBody CreateStoreRequest newStore) {

        storeMetaService.createStore(newStore);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 협업지점 등록 성공"
                ));
    }

    @PatchMapping("/admin/stores/{storeId}")
    public ResponseEntity<CustomResponse> updateStore(@PathVariable long storeId, @RequestBody UpdateStoreRequest updateStore) {

        storeDetailService.updateStore(storeId, updateStore);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 정보 수정 성공"
                ));
    }

    @GetMapping("/admin/stores/{storeId}/images")
    public ResponseEntity<CustomResponse<AllImageUrlResponse>> findAllImages(@PathVariable Long storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 이미지 전체 조회 성공",
                        storeImageService.findAllImages(storeId)
                ));
    }

    @PostMapping(value = "/admin/stores/{storeId}/images", consumes = {"multipart/form-data"})
    public ResponseEntity<CustomResponse> uploadStoreImage(@RequestPart MultipartFile image, @PathVariable long storeId) {

        storeImageService.uploadFile(image, storeId, storeImageService.makeRandomId());

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 이미지 업로드 성공"
                ));
    }

    @DeleteMapping("/admin/stores/images/{imageId}")
    public ResponseEntity<CustomResponse> deleteStoreImage(@PathVariable long imageId) {

        storeImageService.deleteFile(imageId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 이미지 삭제 성공"
                ));
    }

    @DeleteMapping("/admin/stores/{storeId}")
    public ResponseEntity<CustomResponse> deleteStore(@PathVariable long storeId) {

        storeMetaService.deleteStoreMeta(storeId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 삭제 성공"
                ));
    }

    @GetMapping("/stores/classifications")
    public ResponseEntity<CustomResponse<AllClassificationResponse>> findAllClassification() {

        AllClassificationResponse classifications = classificationService.findAllClassification();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대분류 전체 조회 성공",
                        classifications));
    }

    @PostMapping("/admin/stores/classifications")
    public ResponseEntity<CustomResponse> createClassification(@RequestBody CreateClassificationRequest newClassification) {

        classificationService.createClassification(newClassification);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 대분류 등록 성공"
                ));
    }

    @DeleteMapping("/admin/stores/classifications/{classificationId}")
    public ResponseEntity<CustomResponse> deleteClassification(@PathVariable long classificationId) {

        classificationService.deleteClassification(classificationId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대분류 삭제 성공"
                ));
    }

    @GetMapping("/stores/subClassifications")
    public ResponseEntity<CustomResponse<AllSubClassificationResponse>> findAllSubClassification() {

        AllSubClassificationResponse subClassifications = classificationService.findAllSubClassification();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "소분류 전체 조회 성공",
                        subClassifications));
    }

    @PostMapping("/admin/stores/subClassifications")
    public ResponseEntity<CustomResponse> createSubClassification(@RequestBody CreateSubClassificationRequest newSubClassification) {

        classificationService.createSubClassification(newSubClassification);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 소분류 등록 성공"
                ));
    }

    @DeleteMapping("/admin/stores/subClassifications/{subClassificationId}")
    public ResponseEntity<CustomResponse> deleteSubClassification(@PathVariable long subClassificationId) {

        classificationService.deleteSubClassification(subClassificationId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "소분류 삭제 성공"
                ));
    }

    @GetMapping("/stores/introductions")
    public ResponseEntity<CustomResponse<AllStoreIntroductionResponse>> findAllStoreMeta() {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 메타 전체 조회 성공",
                        storeDetailService.findAllStoreIntroductions()));
    }

    @PatchMapping("/admin/stores/{storeId}/activate")
    public ResponseEntity<CustomResponse> activateStoreStatus(@PathVariable long storeId) {

        storeMetaService.activateStoreStatus(storeId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 활성화 성공"
                ));
    }

    @PatchMapping("/admin/stores/{storeId}/inactivate")
    public ResponseEntity<CustomResponse> inActivateStoreStatus(@PathVariable long storeId) {

        storeMetaService.inactivateStoreStatus(storeId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 비활성화 성공"
                ));
    }
}
