package upbrella.be.store.entity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import upbrella.be.store.dto.response.ImageSizeUrls
import upbrella.be.store.dto.response.ImageUrlsResponse
import javax.persistence.*

@Entity
class StoreImage(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_detail_id")
    val storeDetail: StoreDetail? = null,
    var imageUrl: String? = null,

    @Column(name = "image_urls", columnDefinition = "TEXT")
    var imageUrls: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    companion object {
        private val objectMapper: ObjectMapper = jacksonObjectMapper()

        @JvmStatic
        fun createStoreImage(storeDetail: StoreDetail, imageUrl: String): StoreImage {
            return StoreImage(
                storeDetail = storeDetail,
                imageUrl = imageUrl,
            )
        }

        @JvmStatic
        fun createStoreImageWithUrls(
            storeDetail: StoreDetail,
            imageUrlsResponse: ImageUrlsResponse
        ): StoreImage {
            return StoreImage(
                storeDetail = storeDetail,
                imageUrl = imageUrlsResponse.jpeg.medium, // 기존 호환성
                imageUrls = objectMapper.writeValueAsString(imageUrlsResponse),
            )
        }
    }

    /**
     * 다중 URL 구조 반환 (JSON 파싱 또는 기존 URL fallback)
     */
    fun getImageUrlsResponse(): ImageUrlsResponse {
        return if (imageUrls != null) {
            // 새 포맷: JSON 파싱
            objectMapper.readValue(imageUrls, ImageUrlsResponse::class.java)
        } else {
            // 기존 이미지: 단일 URL을 모든 크기에 사용
            ImageUrlsResponse(
                id = id,
                webp = null,
                jpeg = ImageSizeUrls(
                    thumb = imageUrl ?: "",
                    medium = imageUrl ?: "",
                    large = imageUrl ?: ""
                )
            )
        }
    }
}