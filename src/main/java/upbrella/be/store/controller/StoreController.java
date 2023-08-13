package upbrella.be.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upbrella.be.store.dto.request.*;
import upbrella.be.store.dto.response.*;
import upbrella.be.store.service.ClassificationService;
import upbrella.be.store.service.StoreDetailService;
import upbrella.be.store.service.StoreImageService;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreImageService storeImageService;
    private final StoreMetaService storeMetaService;
    private final ClassificationService classificationService;
    private final StoreDetailService storeDetailService;

    @GetMapping("/{storeId}")
    public ResponseEntity<CustomResponse<StoreFindByIdResponse>> findStoreById(HttpSession session, @PathVariable long storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "가게 조회 성공",
                        storeDetailService.findStoreDetailByStoreMetaId(storeId)));
    }

    @GetMapping("/location")
    public ResponseEntity<CustomResponse<AllCurrentLocationStoreResponse>> findCurrentLocationStore(HttpSession session, @ModelAttribute CoordinateRequest coordinateRequest) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "현재 위치 기준 가게 조회 성공",
                        storeMetaService.findStoresInCurrentMap(coordinateRequest, LocalDateTime.now())));
    }

    @GetMapping("/location/{umbrellaId}")
    public ResponseEntity<CustomResponse<CurrentUmbrellaStoreResponse>> findCurrentUmbrellaStore(HttpSession session, @PathVariable long umbrellaId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "현재 우산 가게 조회 성공",
                        storeMetaService.findCurrentStoreIdByUmbrella(umbrellaId)));
    }


    @GetMapping
    public ResponseEntity<CustomResponse<AllStoreResponse>> findAllStores(HttpSession session) {

        List<SingleStoreResponse> allStores = storeDetailService.findAllStores();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "가게 전체 조회 성공",
                        AllStoreResponse.builder()
                                .stores(allStores)
                                .build()
                ));
    }

    @PostMapping
    public ResponseEntity<CustomResponse> createStore(HttpSession session, @RequestBody CreateStoreRequest newStore) {

        storeMetaService.createStore(newStore);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 협업지점 등록 성공"
                ));
    }

    @PatchMapping("/{storeId}")
    public ResponseEntity<CustomResponse> updateStore(HttpSession session, @PathVariable long storeId, @RequestBody UpdateStoreRequest updateStore) {

        storeDetailService.updateStore(storeId, updateStore);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 정보 수정 성공"
                ));
    }

    @PostMapping(value = "/{storeId}/images", consumes = {"multipart/form-data"})
    public ResponseEntity<CustomResponse> uploadStoreImage(HttpSession session, @RequestPart MultipartFile image, @PathVariable long storeId) {

        storeImageService.uploadFile(image, storeId, storeImageService.makeRandomId());

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 이미지 업로드 성공"
                ));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<CustomResponse> deleteStoreImage(HttpSession session, @PathVariable long imageId) {

        storeImageService.deleteFile(imageId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 이미지 삭제 성공"
                ));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<CustomResponse> deleteStore(HttpSession session, @PathVariable long storeId) {

        storeMetaService.deleteStoreMeta(storeId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 삭제 성공"
                ));
    }

    @GetMapping("/classifications")
    public ResponseEntity<CustomResponse<AllClassificationResponse>> findAllClassification(HttpSession session) {

        AllClassificationResponse classifications = classificationService.findAllClassification();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대분류 전체 조회 성공",
                        classifications));
    }

    @PostMapping("/classifications")
    public ResponseEntity<CustomResponse> createClassification(HttpSession session, @RequestBody CreateClassificationRequest newClassification) {

        classificationService.createClassification(newClassification);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 대분류 등록 성공"
                ));
    }

    @DeleteMapping("/classifications/{classificationId}")
    public ResponseEntity<CustomResponse> deleteClassification(HttpSession session, @PathVariable long classificationId) {

        classificationService.deleteClassification(classificationId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "대분류 삭제 성공"
                ));
    }

    @GetMapping("/subClassifications")
    public ResponseEntity<CustomResponse<AllSubClassificationResponse>> findAllSubClassification(HttpSession session) {

        AllSubClassificationResponse subClassifications = classificationService.findAllSubClassification();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "소분류 전체 조회 성공",
                        subClassifications));
    }

    @PostMapping("/subClassifications")
    public ResponseEntity<CustomResponse> createSubClassification(@RequestBody CreateSubClassificationRequest newSubClassification, HttpSession session) {

        classificationService.createSubClassification(newSubClassification);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 소분류 등록 성공"
                ));
    }

    @DeleteMapping("/subClassifications/{subClassificationId}")
    public ResponseEntity<CustomResponse> deleteSubClassification(@PathVariable long subClassificationId, HttpSession session) {

        classificationService.deleteSubClassification(subClassificationId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "소분류 삭제 성공"
                ));
    }
}
