package upbrella.be.umbrella.entity

import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
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

    fun rentUmbrella() {
        changeStatus(UmbrellaStatus.RENTED)
    }

    fun returnUmbrella(storeMeta: StoreMeta) {
        this.storeMeta = storeMeta
        changeStatus(UmbrellaStatus.AVAILABLE)
    }

    fun cannotBeRented(): Boolean {
        return deleted || status != UmbrellaStatus.AVAILABLE
    }

    private fun changeStatus(status: UmbrellaStatus) {
        this.status = status
        this.rentable = status == UmbrellaStatus.AVAILABLE
        this.missed = status.isMissing()
    }
}
