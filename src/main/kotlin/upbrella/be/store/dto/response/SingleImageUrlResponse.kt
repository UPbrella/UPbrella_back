package upbrella.be.store.dto.response

import upbrella.be.store.entity.StoreImage

data class SingleImageUrlResponse(
    val id: Long?,
    val imageUrl: String?, // 기존 호환성 유지
    val imageUrls: ImageUrlsResponse
) {
    companion object {
        fun createImageUrlResponse(storeImage: StoreImage): SingleImageUrlResponse {
            val imageUrlsResponse = storeImage.getImageUrlsResponse()
            return SingleImageUrlResponse(
                id = storeImage.id,
                imageUrl = storeImage.imageUrl, // 기존 호환성
                imageUrls = imageUrlsResponse
            )
        }
    }
}
