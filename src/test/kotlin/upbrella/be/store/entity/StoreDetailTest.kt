package upbrella.be.store.entity

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll

class StoreDetailTest {

    @Test
    @DisplayName("협업지점 이미지 고유번호로 정렬된 이미지들을 조회할 수 있다.")
    fun createImageUrlResponseTest() {
        // given
        val first = StoreImage.builder()
            .id(1L)
            .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
            .build()

        val second = StoreImage.builder()
            .id(2L)
            .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
            .build()

        val images = listOf(second, first)
        val storeDetail = StoreDetail.builder()
            .storeImages(images)
            .build()

        // when
        val sortedStoreImages = storeDetail.getSortedStoreImages()

        // then
        assertAll(
            { assertThat(sortedStoreImages[0].imageUrl).isEqualTo(first.imageUrl) },
            { assertThat(sortedStoreImages[1].imageUrl).isEqualTo(second.imageUrl) }
        )
    }
}