package upbrella.be.store.dto.response

data class ImageUrlsResponse(
    val id: Long?,
    val webp: ImageSizeUrls?,
    val jpeg: ImageSizeUrls
)

data class ImageSizeUrls(
    val thumb: String,
    val medium: String,
    val large: String
)
