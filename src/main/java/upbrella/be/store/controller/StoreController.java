package upbrella.be.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upbrella.be.store.dto.request.CoordinateRequest;
import upbrella.be.store.dto.request.CreateClassificationRequest;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.CreateSubClassificationRequest;
import upbrella.be.store.dto.response.*;
import upbrella.be.store.service.ClassificationService;
import upbrella.be.store.service.StoreImageService;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreImageService storeImageService;
    private final StoreMetaService storeMetaService;
    private final ClassificationService classificationService;

    @GetMapping("/{storeId}")
    public ResponseEntity<CustomResponse<StoreFindByIdResponse>> findStoreById(HttpSession session, @PathVariable long storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "가게 조회 성공",
                        StoreFindByIdResponse.builder()
                                .id(1)
                                .name("업브렐라")
                                .businessHours("09:00 ~ 18:00")
                                .contactNumber("010-0000-0000")
                                .address("서울특별시 강남구 테헤란로 427")
                                .availableUmbrellaCount(10)
                                .openStatus(true)
                                .latitude("37.503716")
                                .longitude("127.053718")
                                .build()));
    }

    @GetMapping("/location")
    public ResponseEntity<CustomResponse<AllCurrentLocationStoreResponse>> findCurrentLocationStore(HttpSession session, @ModelAttribute CoordinateRequest coordinateRequest) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "현재 위치 기준 가게 조회 성공",
                        AllCurrentLocationStoreResponse.builder()
                                .stores(List.of(
                                        SingleCurrentLocationStoreResponse.builder()
                                                .id(1)
                                                .name("업브렐라")
                                                .latitude(37.503716)
                                                .longitude(127.053718)
                                                .openStatus(true)
                                                .build()))
                                .build()));
    }

    @GetMapping("/location/{umbrellaId}")
    public ResponseEntity<CustomResponse<CurrentUmbrellaStoreResponse>> findCurrentUmbrellaStore(HttpSession session, @PathVariable long umbrellaId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "현재 우산 가게 조회 성공",
                        CurrentUmbrellaStoreResponse.builder()
                                .id(1)
                                .name("업브렐라")
                                .build()));
    }

    @GetMapping
    public ResponseEntity<CustomResponse<AllStoreResponse>> findAllStores(HttpSession session) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "가게 전체 조회 성공",
                        AllStoreResponse.builder()
                                .stores(List.of(
                                        SingleStoreResponse.builder()
                                                .id(1)
                                                .name("업브렐라")
                                                .category("카페")
                                                .classification("신촌")
                                                .subClassification("연세대학교")
                                                .activateStatus(true)
                                                .address("서울특별시 강남구 테헤란로 427")
                                                .umbrellaLocation("가게 앞")
                                                .businessHours("09:00 ~ 18:00")
                                                .contactNumber("010-0000-0000")
                                                .instagramId("upbrella")
                                                .latitude(37.503716)
                                                .longitude(127.053718)
                                                .content("업브렐라입니다.")
                                                .imageUrls(
                                                        List.of(
                                                                "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/1.jpg",
                                                                "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/2.jpg",
                                                                "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/3.jpg"
                                                        )
                                                )
                                                .build()
                                ))
                                .build()));
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
    public ResponseEntity<CustomResponse> updateStore(HttpSession session, @PathVariable long storeId, @RequestBody CreateStoreRequest updateStore) {

        storeMetaService.updateStore(storeId, updateStore);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 정보 수정 성공"
                ));
    }

    // TODO : 협업지점의 사진 조회해서 삭제 후 등록한다.
    @PostMapping(value = "/{storeId}/images", consumes = {"multipart/form-data"})
    public ResponseEntity<CustomResponse<ImageUrlsResponse>> uploadStoreImages(HttpSession session, @RequestPart List<MultipartFile> images, @PathVariable long storeId) {

        List<String> imageUrls = new ArrayList<>();
        storeImageService.deleteImagesBeforeSave(storeId);
        for (MultipartFile image : images) {
            String randomId = storeImageService.makeRandomId();
            imageUrls.add(storeImageService.uploadFile(image, storeId, randomId));
        }

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 이미지 업로드 성공",
                        ImageUrlsResponse.builder()
                                .imageUrls(imageUrls)
                                .build()
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

        AllClassificationResponse classifications = classificationService.findAllClassification("classification");

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

        AllSubClassificationResponse subClassifications = classificationService.findAllSubClassification("subClassification");

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "소분류 전체 조회 성공",
                        subClassifications));
    }

    @PostMapping("/subClassifications")
    public ResponseEntity<CustomResponse> createSubClassification(HttpSession session, @RequestBody CreateSubClassificationRequest newSubClassification) {

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
    public ResponseEntity<CustomResponse> deleteSubClassification(HttpSession session, @PathVariable long subClassificationId) {

        classificationService.deleteClassification(subClassificationId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "소분류 삭제 성공"
                ));
    }
}
