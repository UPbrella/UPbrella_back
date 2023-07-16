package upbrella.be.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.response.*;
import upbrella.be.util.CustomResponse;

import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {

    @GetMapping("/{storeId}")
    public ResponseEntity<CustomResponse<StoreFindByIdResponse>> findStoreById(@PathVariable int storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "가게 조회 성공",
                        StoreFindByIdResponse.builder()
                                .id(1L)
                                .name("업브렐라")
                                .businessHours("09:00 ~ 18:00")
                                .contactNumber("010-0000-0000")
                                .address("서울특별시 강남구 테헤란로 427")
                                .availableUmbrellaCount(10)
                                .openStatus(true)
                                .coordinate("37.503716, 127.053718")
                                .build()));
    }

    @GetMapping("/location/{umbrellaName}")
    public ResponseEntity<CustomResponse<CurrentUmbrellaStore>> findCurrentUmbrellaStore(@PathVariable String umbrellaName) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "현재 우산 가게 조회 성공",
                        CurrentUmbrellaStore.builder()
                                .storeId(1L)
                                .storeName("업브렐라")
                                .build()));
    }

    @GetMapping
    public ResponseEntity<CustomResponse<AllStoreResponse>> findAllStores() {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "가게 전체 조회 성공",
                        AllStoreResponse.builder()
                                .stores(List.of(
                                        SingleStoreResponse.builder()
                                                .id(1L)
                                                .name("업브렐라")
                                                .classification("신촌")
                                                .activateStatus(true)
                                                .address("서울특별시 강남구 테헤란로 427")
                                                .umbrellaLocation("가게 앞")
                                                .businessHours("09:00 ~ 18:00")
                                                .contactNumber("010-0000-0000")
                                                .instagramId("upbrella")
                                                .coordinate("37.503716, 127.053718")
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
    public ResponseEntity<CustomResponse> createStore(@RequestBody CreateStoreRequest newStore) {
        // TODO : form data 형식인지, json 형식으로 들어오는지
        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "새로운 협업지점 등록 성공"
                ));
    }

    @PatchMapping("/{storeId}")
    public ResponseEntity<CustomResponse> updateStore(@PathVariable int storeId, CreateStoreRequest updateStore) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 정보 수정 성공"
                ));
    }

    @PostMapping("/images")
    public ResponseEntity<CustomResponse<ImageUrls>> uploadStoreImages(List<MultipartFile> images) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 이미지 업로드 성공",
                        ImageUrls.builder()
                                .imageUrls(List.of(
                                        "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/1.jpg",
                                        "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/2.jpg",
                                        "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/3.jpg"
                                ))
                                .build()
                ));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<CustomResponse> deleteStore(@PathVariable int storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 삭제 성공"
                ));
    }
}
