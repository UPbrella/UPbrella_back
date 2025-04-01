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
import upbrella.be.store.entity.StoreDetail
import upbrella.be.store.entity.StoreImage
import upbrella.be.store.exception.NonExistingStoreImageException
import upbrella.be.store.repository.StoreImageRepository
import java.util.Optional
import org.mockito.BDDMockito.*
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest

@Transactional
@ExtendWith(MockitoExtension::class)
class StoreImageServiceTest {

    @Mock
    private lateinit var s3Client: S3Client

    @Mock
    private lateinit var storeImageRepository: StoreImageRepository

    @Mock
    private lateinit var storeDetailService: StoreDetailService

    @InjectMocks
    private lateinit var storeImageService: StoreImageService

    @Test
    @DisplayName("파일 업로드 테스트")
    fun uploadFileTest() {
        // given
        val storeDetailId = 1L
        val file = MockMultipartFile("image", "filename.jpg", "image/jpg", "some-image".toByteArray())
        val storeDetail = StoreDetail()
        val randomId = storeImageService.makeRandomId()
        val expectedUrl = "https://file.upbrella.co.kr/store-image/filename.jpg$randomId"

        given(storeDetailService.findByStoreMetaId(storeDetailId)).willReturn(storeDetail)
        given(s3Client.putObject(any(PutObjectRequest::class.java), any(RequestBody::class.java)))
            .willReturn(PutObjectResponse.builder().build())

        // when
        val result = storeImageService.uploadFile(file, storeDetailId, randomId)

        // then
        assertThat(result).isEqualTo(expectedUrl)
        verify(s3Client, times(1))
            .putObject(any(PutObjectRequest::class.java), any(RequestBody::class.java))
        verify(storeImageRepository, times(1)).save(any(StoreImage::class.java))
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

        given(storeImageRepository.findById(testId))
            .willReturn(Optional.of(testImage))

        // when
        storeImageService.deleteFile(testId)

        // then
        assertAll(
            { assertEquals(testId, testImage.id) },
            { assertEquals(testUrl, testImage.imageUrl) },
            { verify(storeImageRepository, times(1)).deleteById(testImage.id!!) },
            { verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest::class.java)) },
        )
    }

    @Test
    @DisplayName("사진이 존재하지 않는데 삭제를 시도하는 경우 예외를 발생시킨다.")
    fun notExistImageDeleteTest() {
        // given
        val imageId = 1L

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
            val fist = SingleImageUrlResponse.builder()
                .id(1L)
                .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
                .build()

            val second = SingleImageUrlResponse.builder()
                .id(2L)
                .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
                .build()

            val imageUrls = listOf(fist, second)

            // when
            val thumbnail = storeImageService.createThumbnail(imageUrls)

            // then
            assertThat(thumbnail).isEqualTo(imageUrls[0].imageUrl)
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
