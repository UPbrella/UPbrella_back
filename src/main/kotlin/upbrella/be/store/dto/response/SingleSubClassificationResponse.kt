package upbrella.be.store.dto.response

import com.querydsl.core.annotations.QueryProjection
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.ClassificationType

data class SingleSubClassificationResponse @QueryProjection constructor(
    val id: Long,
    val type: ClassificationType,
    val name: String
) {

    companion object {
        fun ofCreateSubClassification(classification: Classification) =
            SingleSubClassificationResponse(
                classification.id ?: throw IllegalArgumentException("ID must not be null"),
                classification.type ?: throw IllegalArgumentException("Type must not be null"),
                classification.name ?: throw IllegalArgumentException("Name must not be null")
            )
    }
}
