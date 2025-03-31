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
    var rentable: Boolean,
    var deleted: Boolean,
    val createdAt: LocalDateTime,
    var etc: String,
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
                rentable = request.isRentable,
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
        this.rentable = request.isRentable
        this.etc = request.etc
        this.missed = request.isMissed
    }

    fun rentUmbrella() {
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