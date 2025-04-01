package upbrella.be.store.entity

import upbrella.be.store.dto.request.CreateClassificationRequest
import upbrella.be.store.dto.request.CreateSubClassificationRequest
import javax.persistence.*

@Entity
class Classification(
    @Enumerated(EnumType.STRING)
    val type: ClassificationType? = null,
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    companion object {
        @JvmStatic
        fun ofCreateClassification(request: CreateClassificationRequest): Classification {
            return Classification(
                type = ClassificationType.CLASSIFICATION,
                name = request.name,
                latitude = request.latitude,
                longitude = request.longitude,
            )
        }

        @JvmStatic
        fun ofCreateSubClassification(request: CreateSubClassificationRequest): Classification {
            return Classification(
                type = ClassificationType.SUB_CLASSIFICATION,
                name = request.name,
            )
        }
    }
}