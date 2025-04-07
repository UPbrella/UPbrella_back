package upbrella.be.store.dto.response

data class AllStoreIntroductionResponse(
    val storesByClassification: List<StoreIntroductionsResponseByClassification>
) {
    companion object {
        fun of(storesByClassification: List<StoreIntroductionsResponseByClassification>) =
            AllStoreIntroductionResponse(storesByClassification)
    }
}
