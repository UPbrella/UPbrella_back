package upbrella.be.store.dto.response

import upbrella.be.store.entity.StoreDetail

data class StoreIntroductionsResponseByClassification(
    val subClassificationId: Long,
    val stores: List<SingleStoreIntroductionResponse>
) {
    companion object {
        fun of(subClassificationId: Long, storeDetails: List<StoreDetail>): StoreIntroductionsResponseByClassification {
            return StoreIntroductionsResponseByClassification(
                subClassificationId = subClassificationId,
                stores = storeDetails.map { SingleStoreIntroductionResponse.createSingleIntroduction(it) }
            )
        }
    }
}
