package upbrella.be.store.dto.response

import com.querydsl.core.annotations.QueryProjection
import java.io.Serializable

data class SingleStoreResponse @QueryProjection constructor(
    val id: Long,
    val name: String,
    val category: String,
    val classification: SingleClassificationResponse,
    val subClassification: SingleSubClassificationResponse,
    val activateStatus: Boolean,
    val address: String,
    val addressDetail: String,
    val umbrellaLocation: String,
    val businessHour: String,
    val contactNumber: String,
    val instagramId: String,
    val latitude: Double,
    val longitude: Double,
    val content: String
) : Serializable
