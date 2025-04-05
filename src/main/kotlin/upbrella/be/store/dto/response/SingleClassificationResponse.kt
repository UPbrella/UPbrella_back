package upbrella.be.store.dto.response

import com.querydsl.core.annotations.QueryProjection
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.ClassificationType

data class SingleClassificationResponse @QueryProjection constructor(
    val id: Long?,
    val type: ClassificationType?,
    val name: String?,
    val latitude: Double?,
    val longitude: Double?
) {
    companion object {

        fun ofCreateClassification(classification: Classification): SingleClassificationResponse {
            return SingleClassificationResponse(
                id = classification.id,
                type = classification.type,
                name = classification.name,
                latitude = classification.latitude,
                longitude = classification.longitude
            )
        }
    }
}
