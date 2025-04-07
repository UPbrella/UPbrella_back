package upbrella.be.store.dto.response

import upbrella.be.store.entity.StoreImage

data class SingleImageUrlResponse(
    val id: Long?,
    val imageUrl: String?
) {
    companion object {
        fun createImageUrlResponse(imageUrl: StoreImage) = SingleImageUrlResponse(
            id = imageUrl.id,
            imageUrl = imageUrl.imageUrl
        )
    }
}
