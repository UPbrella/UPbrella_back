package upbrella.be.store.entity

import upbrella.be.store.dto.request.CreateStoreRequest
import upbrella.be.store.dto.request.UpdateStoreRequest
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class StoreMeta(
    var name: String,
    var activated: Boolean,
    var deleted: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classification_id")
    var classification: Classification? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_classification_id")
    var subClassification: Classification? = null,
    var category: String,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    @OneToMany(mappedBy = "storeMeta")
    val businessHours: List<BusinessHour> = emptyList(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    companion object {
        @JvmStatic
        fun createStoreMetaForSave(
            request: CreateStoreRequest,
            classification: Classification,
            subClassification: Classification
        ): StoreMeta {
            return StoreMeta(
                name = request.name,
                activated = request.activateStatus,
                classification = classification,
                subClassification = subClassification,
                category = request.category,
                latitude = request.latitude,
                longitude = request.longitude
            )
        }

        @JvmStatic
        fun createStoreMetaForUpdate(
            request: UpdateStoreRequest,
            classification: Classification,
            subClassification: Classification
        ): StoreMeta {
            return StoreMeta(
                name = request.name,
                classification = classification,
                subClassification = subClassification,
                category = request.category,
                latitude = request.latitude,
                longitude = request.longitude,
                // 이건 안쓰이는듯
                activated = false,
            )
        }
    }

    fun updateStoreMeta(storeMeta: StoreMeta) {
        this.name = storeMeta.name
        this.deleted = storeMeta.deleted
        this.classification = storeMeta.classification
        this.subClassification = storeMeta.subClassification
        this.category = storeMeta.category
        this.latitude = storeMeta.latitude
        this.longitude = storeMeta.longitude
    }

    fun delete() {
        this.classification = null
        this.subClassification = null
        this.deleted = true
    }

    fun isOpenStore(currentTime: LocalDateTime): Boolean {
        return businessHours.stream()
            .filter { businessHour -> businessHour.date == currentTime.dayOfWeek }
            .filter { _ -> this.activated }
            .anyMatch { businessHour ->
                currentTime.toLocalTime().isAfter(businessHour.openAt)
                        && currentTime.toLocalTime().isBefore(businessHour.closeAt)
            }
    }

    fun activateStoreStatus() {
        this.activated = true
    }

    fun inactivateStoreStatus() {
        this.activated = false
    }
}