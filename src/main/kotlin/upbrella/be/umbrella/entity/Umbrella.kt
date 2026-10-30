package upbrella.be.umbrella.entity

import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
import upbrella.be.umbrella.exception.MissingUmbrellaException
import upbrella.be.umbrella.exception.NotAvailableUmbrellaException
import upbrella.be.umbrella.exception.UmbrellaStoreMissMatchException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Umbrella(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_meta_id")
    var storeMeta: StoreMeta,
    var uuid: Long,
    status: UmbrellaStatus,
    var deleted: Boolean,
    val createdAt: LocalDateTime,
    var etc: String?,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var inventoryVerifiedAt: LocalDateTime? = null,
) {

    @Enumerated(EnumType.STRING)
    var status: UmbrellaStatus = status
        private set

    // 하위 호환용 컬럼. status가 바뀔 때 같이 바뀐다. FE가 status로 옮기면 지운다
    var rentable: Boolean = status == UmbrellaStatus.AVAILABLE
        private set

    var missed: Boolean = status.isMissing()
        private set

    companion object {
        @JvmStatic
        fun ofCreated(request: UmbrellaCreateRequest, storeMeta: StoreMeta): Umbrella {
            return Umbrella(
                storeMeta = storeMeta,
                uuid = request.uuid,
                status = request.toStatus(),
                deleted = false,
                createdAt = LocalDateTime.now(),
                etc = request.etc
            )
        }
    }

    fun delete() {
        this.deleted = true
    }

    fun update(request: UmbrellaModifyRequest, storeMeta: StoreMeta) {
        this.storeMeta = storeMeta
        this.uuid = request.uuid
        changeStatus(request.toStatus(status))
        this.etc = request.etc
    }

    // 빌리려는 매장에 있는 사용 가능 우산만 대여중으로 바꾼다
    fun rentUmbrella(storeId: Long) {
        if (storeMeta.id != storeId) {
            throw UmbrellaStoreMissMatchException("[ERROR] 해당 우산은 해당 매장에 존재하지 않습니다.")
        }
        when (status) {
            UmbrellaStatus.AVAILABLE -> changeStatus(UmbrellaStatus.RENTED)
            UmbrellaStatus.RENTED -> throw NotAvailableUmbrellaException("[ERROR] 해당 우산은 대여중입니다.")
            UmbrellaStatus.UNLOCATED, UmbrellaStatus.LOST ->
                throw MissingUmbrellaException("[ERROR] 해당 우산은 분실되었습니다.")
        }
    }

    fun returnUmbrella(storeMeta: StoreMeta) {
        this.storeMeta = storeMeta
        changeStatus(UmbrellaStatus.AVAILABLE)
    }

    fun cannotBeRented(): Boolean {
        return deleted || status != UmbrellaStatus.AVAILABLE
    }

    fun reconcileInventory(status: UmbrellaStatus, verifiedAt: LocalDateTime?) {
        changeStatus(status)
        if (verifiedAt != null) inventoryVerifiedAt = verifiedAt
    }

    private fun changeStatus(status: UmbrellaStatus) {
        this.status = status
        this.rentable = status == UmbrellaStatus.AVAILABLE
        this.missed = status.isMissing()
    }
}
