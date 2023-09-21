package upbrella.be.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import upbrella.be.rent.dto.request.HistoryFilterRequest;
import upbrella.be.rent.dto.response.RentalHistoriesPageResponse;
import upbrella.be.rent.service.RentService;
import upbrella.be.store.dto.response.AllStoreResponse;
import upbrella.be.store.dto.response.SingleStoreResponse;
import upbrella.be.store.service.StoreDetailService;

import java.util.List;

@Profile("dev")
@Controller
@RequiredArgsConstructor
public class NGrinderController {

    private final RentService rentService;
    private final StoreDetailService storeDetailService;

    @GetMapping("/nGrinder/storeTest")
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

    @GetMapping("/nGrinder/historyTest")
    public ResponseEntity<CustomResponse<RentalHistoriesPageResponse>> findRentalHistory(@ModelAttribute HistoryFilterRequest filter, Pageable pageable) {

        RentalHistoriesPageResponse histories = rentService.findAllHistories(filter, pageable);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "어드민 대여 내역 조회 성공",
                        histories
                ));
    }
}
