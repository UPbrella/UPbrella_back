package upbrella.be.umbrella.entity

import upbrella.be.rent.exception.NotAvailableUmbrellaException
import upbrella.be.rent.exception.UmbrellaStoreMissMatchException
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
import upbrella.be.umbrella.exception.MissingUmbrellaException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Umbrella(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_meta_id")
    var storeMeta: StoreMeta,
    var uuid: Long,
    var rentable: Boolean,
    var deleted: Boolean,
    val createdAt: LocalDateTime,
    var etc: String?,
    var missed: Boolean,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {

    companion object {
        @JvmStatic
        fun ofCreated(request: UmbrellaCreateRequest, storeMeta: StoreMeta): Umbrella {
            return Umbrella(
                storeMeta = storeMeta,
                uuid = request.uuid,
                rentable = request.rentable,
                deleted = false,
                createdAt = LocalDateTime.now(),
                etc = request.etc,
                missed = false
            )
        }
    }

    fun delete() {
        this.deleted = true
    }

    fun update(request: UmbrellaModifyRequest, storeMeta: StoreMeta) {
        this.storeMeta = storeMeta
        this.uuid = request.uuid
        this.rentable = request.rentable
        this.etc = request.etc
        this.missed = request.missed
    }

    fun rentUmbrella(storeIdForRent: Long) {
        if (this.storeMeta.id != storeIdForRent) {
            throw UmbrellaStoreMissMatchException("[ERROR] 해당 우산은 해당 매장에 존재하지 않습니다.")
        }
        if (this.missed) {
            throw MissingUmbrellaException("[ERROR] 해당 우산은 분실되었습니다.")
        }
        if (!this.rentable) {
            throw NotAvailableUmbrellaException("[ERROR] 해당 우산은 대여중입니다.")
        }
        this.rentable = false
    }

    fun returnUmbrella(storeMeta: StoreMeta) {
        this.storeMeta = storeMeta
        this.rentable = true
    }

    fun cannotBeRented(): Boolean {
        return missed || deleted || !rentable
    }
}
