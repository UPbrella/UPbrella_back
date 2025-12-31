package upbrella.be.store.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import upbrella.be.store.dto.response.SingleImageUrlResponse
import upbrella.be.store.dto.response.ImageUrlsResponse
import upbrella.be.store.dto.response.ImageSizeUrls
import upbrella.be.store.entity.StoreDetail
import upbrella.be.store.entity.StoreImage
import upbrella.be.store.exception.NonExistingStoreImageException
import org.mockito.BDDMockito.*
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import upbrella.be.store.repository.StoreDetailReader
import upbrella.be.store.repository.StoreImageReader
import upbrella.be.store.repository.StoreImageWriter
import upbrella.be.store.service.ImageProcessingService
import upbrella.be.store.service.ProcessedImageSet
import upbrella.be.store.service.ImageVariantData
import org.springframework.web.multipart.MultipartFile

@Transactional
@ExtendWith(MockitoExtension::class)
class StoreImageServiceTest {

    @Mock
    private lateinit var s3Client: S3Client

    @Mock
    private lateinit var storeImageReader: StoreImageReader

    @Mock
    private lateinit var storeImageWriter: StoreImageWriter

    @Mock
    private lateinit var storeDetailReader: StoreDetailReader

    @Mock
    private lateinit var imageProcessingService: ImageProcessingService

    @InjectMocks
    private lateinit var storeImageService: StoreImageService

    @Test
    @DisplayName("파일 업로드 테스트")
    fun uploadFileTest() {
        // given
        val storeDetailId = 1L
        val file =
            MockMultipartFile("image", "filename.jpg", "image/jpg", "some-image".toByteArray())
        val storeDetail = StoreDetail()
        val randomId = storeImageService.makeRandomId()

        // Mock ProcessedImageSet
        val mockImageData = ImageVariantData(
            webp = "webp-data".toByteArray(),
            jpeg = "jpeg-data".toByteArray()
        )
        val mockProcessedImageSet = ProcessedImageSet(
            thumbnail = mockImageData,
            medium = mockImageData,
            large = mockImageData
        )

        val expectedUrl = "https://file.upbrella.co.kr/store-image/$storeDetailId/${randomId}_medium.jpg"

        given(storeDetailReader.findByStoreMetaId(storeDetailId)).willReturn(storeDetail)
        given(imageProcessingService.processImage(org.mockito.kotlin.any<MultipartFile>()))
            .willReturn(mockProcessedImageSet)
        given(s3Client.putObject(org.mockito.kotlin.any<PutObjectRequest>(), org.mockito.kotlin.any<RequestBody>()))
            .willReturn(PutObjectResponse.builder().build())

        // when
        val result = storeImageService.uploadFile(file, storeDetailId, randomId)

        // then
        assertThat(result).isEqualTo(expectedUrl)
        verify(s3Client, times(6))
            .putObject(org.mockito.kotlin.any<PutObjectRequest>(), org.mockito.kotlin.any<RequestBody>())
        verify(storeImageWriter, times(1)).save(org.mockito.kotlin.any<StoreImage>())
    }

    @Test
    @DisplayName("새로 사진을 등록할 때 기존에 있던 사진들을 삭제하는 테스트")
    fun deleteFileTest() {
        // given
        val testId = 1L
        val testUrl = "http://mybucket.s3.amazonaws.com/myimage.jpg"
        val testImage = StoreImage(
            id = testId,
            imageUrl = testUrl,
        )

        given(storeImageReader.findById(testId))
            .willReturn(testImage)

        // when
        storeImageService.deleteFile(testId)

        // then
        assertAll(
            { assertEquals(testId, testImage.id) },
            { assertEquals(testUrl, testImage.imageUrl) },
            { verify(storeImageWriter, times(1)).deleteById(testImage.id!!) },
            { verify(s3Client, times(3)).deleteObject(any(DeleteObjectRequest::class.java)) }, // 3 sizes x 1 format (JPEG only, no WebP for old images)
        )
    }

    @Test
    @DisplayName("사진이 존재하지 않는데 삭제를 시도하는 경우 예외를 발생시킨다.")
    fun notExistImageDeleteTest() {
        // given
        val imageId = 1L
        given(storeImageReader.findById(imageId)).willReturn(null)

        // when & then
        assertThatThrownBy {
            storeImageService.deleteFile(imageId)
        }
            .isInstanceOf(NonExistingStoreImageException::class.java)
            .hasMessageContaining("해당 이미지가 존재하지 않습니다.")
    }

    @Nested
    @DisplayName("사용자는 ")
    inner class ThumbNail {

        @Test
        @DisplayName("사용자는 썸네일을 생성할 수 있다.")
        fun createThumbnailTest() {
            // given
            val first = SingleImageUrlResponse(
                id = 1L,
                imageUrl = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg",
                imageUrls = ImageUrlsResponse(
                    id = 1L,
                    webp = ImageSizeUrls(
                        thumb = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-thumb.webp",
                        medium = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-medium.webp",
                        large = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-large.webp"
                    ),
                    jpeg = ImageSizeUrls(
                        thumb = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-thumb.jpg",
                        medium = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-medium.jpg",
                        large = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-large.jpg"
                    )
                )
            )

            val second = SingleImageUrlResponse(
                id = 2L,
                imageUrl = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg",
                imageUrls = ImageUrlsResponse(
                    id = 2L,
                    webp = ImageSizeUrls(
                        thumb = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-thumb.webp",
                        medium = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-medium.webp",
                        large = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-large.webp"
                    ),
                    jpeg = ImageSizeUrls(
                        thumb = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-thumb.jpg",
                        medium = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-medium.jpg",
                        large = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename-large.jpg"
                    )
                )
            )

            val imageUrls = listOf(first, second)

            // when
            val thumbnail = storeImageService.createThumbnail(imageUrls)

            // then
            // WebP 썸네일 우선 반환
            assertThat(thumbnail).isEqualTo(imageUrls[0].imageUrls.webp?.thumb)
        }

        @Test
        @DisplayName("사진이 등록되지 않은 경우는 썸네일을 생성하지 않는다.")
        fun emptyThumbnailTest() {
            // given
            val emptyImageUrls = listOf<SingleImageUrlResponse>()

            // when
            val thumbnail = storeImageService.createThumbnail(emptyImageUrls)

            // then
            assertThat(thumbnail).isNull()
        }
    }
}
