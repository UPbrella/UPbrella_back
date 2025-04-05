package upbrella.be.umbrella.controller

import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
import upbrella.be.umbrella.dto.response.UmbrellaPageResponse
import upbrella.be.umbrella.dto.response.UmbrellaStatisticsResponse
import upbrella.be.umbrella.service.UmbrellaService
import upbrella.be.util.CustomResponse
import javax.validation.Valid


@RestController
class UmbrellaController(
    private val umbrellaService: UmbrellaService
) {

    @GetMapping("/admin/umbrellas")
    fun showAllUmbrellas(pageable: Pageable?): ResponseEntity<CustomResponse<UmbrellaPageResponse>> {
        return ResponseEntity
            .ok()
            .body(
                CustomResponse(
                    "success",
                    200,
                    "전체 우산 현황 조회 성공",
                    UmbrellaPageResponse(
                        umbrellaResponsePage = umbrellaService!!.findAllUmbrellas(pageable!!)
                    )
                )
            )
    }

    @GetMapping("/admin/umbrellas/{storeId}")
    fun showUmbrellasByStoreId(
        @PathVariable storeId: Long,
        pageable: Pageable?
    ): ResponseEntity<CustomResponse<UmbrellaPageResponse>> {
        return ResponseEntity
            .ok()
            .body(
                CustomResponse(
                    "success",
                    200,
                    "지점 우산 현황 조회 성공",
                    UmbrellaPageResponse(
                        umbrellaResponsePage = umbrellaService!!.findUmbrellasByStoreId(storeId, pageable!!)
                    )
                )
            )
    }

    @PostMapping("/admin/umbrellas")
    fun addUmbrella(@RequestBody umbrellaCreateRequest: @Valid UmbrellaCreateRequest?): ResponseEntity<CustomResponse<*>> {
        umbrellaService!!.addUmbrella(umbrellaCreateRequest!!)
        return ResponseEntity
            .ok()
            .body(
                CustomResponse(
                    "success",
                    200,
                    "새로운 우산 추가 성공",
                    null
                )
            )
    }

    @PatchMapping("/admin/umbrellas/{id}")
    fun modifyUmbrella(
        @RequestBody umbrellaModifyRequest: @Valid UmbrellaModifyRequest?,
        @PathVariable id: Long
    ): ResponseEntity<CustomResponse<*>> {
        umbrellaService!!.modifyUmbrella(id, umbrellaModifyRequest!!)
        return ResponseEntity
            .ok()
            .body(
                CustomResponse(
                    "success",
                    200,
                    "우산 정보 변경 성공",
                    null
                )
            )
    }

    @DeleteMapping("/admin/umbrellas/{id}")
    fun deleteUmbrella(@PathVariable id: Long): ResponseEntity<CustomResponse<*>> {
        umbrellaService!!.deleteUmbrella(id)
        return ResponseEntity
            .ok()
            .body(
                CustomResponse(
                    "success",
                    200,
                    "우산 삭제 성공",
                    null
                )
            )
    }

    @GetMapping("/admin/umbrellas/statistics")
    fun showAllUmbrellasStatistics(): ResponseEntity<CustomResponse<UmbrellaStatisticsResponse>> {
        return ResponseEntity
            .ok()
            .body(
                CustomResponse(
                    "success",
                    200,
                    "전체 우산 통계 조회 성공",
                    umbrellaService!!.getUmbrellaAllStatistics()
                )
            )
    }

    @GetMapping("/admin/umbrellas/statistics/{storeId}")
    fun showUmbrellasStatisticsByStore(@PathVariable storeId: Long): ResponseEntity<CustomResponse<UmbrellaStatisticsResponse>> {
        return ResponseEntity
            .ok()
            .body(
                CustomResponse(
                    "success",
                    200,
                    "지점 우산 통계 조회 성공",
                    umbrellaService!!.getUmbrellaStatisticsByStoreId(storeId)
                )
            )
    }
}
