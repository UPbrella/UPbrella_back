package upbrella.be.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upbrella.be.store.dto.response.StoreFindByIdResponse;
import upbrella.be.util.CustomResponse;

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
}
