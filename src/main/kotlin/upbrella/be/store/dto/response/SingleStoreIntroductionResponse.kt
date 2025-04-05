package upbrella.be.store.dto.response

import upbrella.be.store.entity.StoreDetail

data class SingleStoreIntroductionResponse(
    val id: Long,
    val thumbnail: String?,
    val name: String?,
    val category: String?
) {
    companion object {
        fun of(id: Long, thumbnail: String?, name: String, category: String): SingleStoreIntroductionResponse {
            return SingleStoreIntroductionResponse(id, thumbnail, name, category)
        }

        fun createSingleIntroduction(storeDetail: StoreDetail): SingleStoreIntroductionResponse {
            val sortedImageUrls = storeDetail.getSortedStoreImages()
                .map { SingleImageUrlResponse.createImageUrlResponse(it) }

            val thumbnail = createThumbnail(sortedImageUrls)
            val storeMeta = storeDetail.storeMeta

            return of(
                id = storeMeta?.id ?: throw IllegalArgumentException("ID must not be null"),
                thumbnail = thumbnail,
                name = storeMeta.name,
                category = storeMeta.category
            )
        }

        private fun createThumbnail(imageUrls: List<SingleImageUrlResponse>): String? {
            return imageUrls.firstOrNull()?.imageUrl
        }
    }
}
