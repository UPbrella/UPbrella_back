package upbrella.be.store.dto.response

data class AllImageUrlResponse(
    val storeId: Long,
    val images: List<SingleImageUrlResponse>
) {
    companion object {
        fun of(storeId: Long, images: List<SingleImageUrlResponse>) =
            AllImageUrlResponse(storeId, images)
    }
}
