package upbrella.be.util

import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import upbrella.be.rent.dto.request.HistoryFilterRequest
import upbrella.be.rent.dto.response.RentalHistoriesPageResponse
import upbrella.be.rent.service.RentService
import upbrella.be.store.dto.response.AllStoreResponse
import upbrella.be.store.dto.response.SingleStoreResponse
import upbrella.be.store.service.StoreDetailService

@Profile("dev")
@Controller
class NGrinderController(
    private val rentService: RentService,
    private val storeDetailService: StoreDetailService
) {

    @GetMapping("/nGrinder/storeTest")
    fun findAllStores(): ResponseEntity<CustomResponse<AllStoreResponse>> {
        val allStores: List<SingleStoreResponse> = storeDetailService.findAllStores()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                status = "success",
                code = 200,
                message = "어드민 가게 전체 조회 성공",
                data = AllStoreResponse(
                    stores = allStores
                )
            ))
    }

    @GetMapping("/nGrinder/historyTest")
    fun findRentalHistory(
        @ModelAttribute filter: HistoryFilterRequest,
        pageable: Pageable
    ): ResponseEntity<CustomResponse<RentalHistoriesPageResponse>> {
        val histories: RentalHistoriesPageResponse = rentService.findAllHistories(filter, pageable)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                status = "success",
                code = 200,
                message = "어드민 대여 내역 조회 성공",
                data = histories
            ))
    }
}
