package upbrella.be.store.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StoreDetailTest {

    @Test
    @DisplayName("협업지점 이미지 고유번호로 정렬된 이미지들을 조회할 수 있다.")
    void createImageUrlResponseTest() {
        // given
        StoreImage first = StoreImage.builder()
                .id(1L)
                .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
                .build();

        StoreImage second = StoreImage.builder()
                .id(2L)
                .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
                .build();

        List<StoreImage> images = List.of(second, first);
        StoreDetail storeDetail = StoreDetail.builder()
                .storeImages(images)
                .build();

        // when

        List<StoreImage> sortedStoreImages = storeDetail.getSortedStoreImages();
        // then
        assertAll(
                () -> assertThat(sortedStoreImages.get(0).getImageUrl()).isEqualTo(first.getImageUrl()),
                () -> assertThat(sortedStoreImages.get(1).getImageUrl()).isEqualTo(second.getImageUrl())
        );
    }
}
