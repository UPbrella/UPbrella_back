package upbrella.be.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upbrella.be.store.dto.request.CoordinateRequest;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.response.*;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {

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
                                                .classification("신촌")
                                                .activateStatus(true)
                                                .address("서울특별시 강남구 테헤란로 427")
                                                .umbrellaLocation("가게 앞")
                                                .businessHours("09:00 ~ 18:00")
                                                .contactNumber("010-0000-0000")
                                                .instagramId("upbrella")
                                                .latitude(37.503716)
                                                .longitude(127.053718)
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
    public ResponseEntity<CustomResponse> updateStore(HttpSession session, @PathVariable long storeId, CreateStoreRequest updateStore) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 정보 수정 성공"
                ));
    }

    @PostMapping("/{storeId}/images")
    public ResponseEntity<CustomResponse<ImageUrlsResponse>> uploadStoreImages(HttpSession session, List<MultipartFile> images,@PathVariable long storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 이미지 업로드 성공",
                        ImageUrlsResponse.builder()
                                .imageUrls(List.of(
                                        "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/1.jpg",
                                        "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/2.jpg",
                                        "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/3.jpg"
                                ))
                                .build()
                ));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<CustomResponse> deleteStore(HttpSession session, @PathVariable long storeId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "협업지점 삭제 성공"
                ));
    }
}
