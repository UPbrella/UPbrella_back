package upbrella.be.umbrella.controller

import org.springframework.web.bind.annotation.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import upbrella.be.umbrella.repository.UmbrellaRepository
import upbrella.be.umbrella.entity.UmbrellaStatus
import upbrella.be.rent.repository.RentRepository
import upbrella.be.util.CustomResponse
import java.time.Clock
import java.time.LocalDateTime

data class InventoryRequest(val verifiedUmbrellaIds: Set<Long> = emptySet(), val apply: Boolean = false)

@RestController
class InventoryController(private val umbrellas: UmbrellaRepository, private val histories: RentRepository, private val clock: Clock) {
    @PostMapping("/admin/umbrellas/reconcile")
    @Transactional
    fun reconcile(@RequestBody request: InventoryRequest): CustomResponse<List<Map<String, Any?>>> {
        val now = LocalDateTime.now(clock)
        val all = umbrellas.findAllByDeletedFalseOrderByIdAsc()
        if (!all.map { it.id }.containsAll(request.verifiedUmbrellaIds)) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 우산이 포함되어 있습니다.")
        val rows = all.map { current ->
            val umbrella = if (request.apply) umbrellas.findByIdAndDeletedIsFalseForUpdate(current.id!!).get() else current
            val latest = histories.findFirstByUmbrellaIdOrderByRentedAtDesc(umbrella.id!!).orElse(null)
            val state = when {
                umbrella.id in request.verifiedUmbrellaIds -> if (latest?.returnedAt == null && latest != null) "RENTED" else "AVAILABLE"
                latest == null -> "UNLOCATED"
                latest.rentedAt.isBefore(now.minusMonths(3)) -> "LOST"
                else -> "UNLOCATED"
            }
            if (request.apply) {
                umbrella.reconcileInventory(UmbrellaStatus.valueOf(state),
                    if (umbrella.id in request.verifiedUmbrellaIds) now else null)
            }
            mapOf<String, Any?>("id" to umbrella.id, "uuid" to umbrella.uuid, "storeName" to umbrella.storeMeta.name,
                "status" to state, "lastRentedAt" to latest?.rentedAt)
        }
        return CustomResponse("success", 200, if (request.apply) "재고 분류 적용 완료" else "재고 분류 미리보기", rows)
    }
}
