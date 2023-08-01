package upbrella.be.store.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import upbrella.be.store.entity.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SingleStoreResponseTest {

    @Nested
    @DisplayName("모든 협업지점 조회 로직에서")
    class storeResponseTest {

        BusinessHour monday = BusinessHour.builder()
                .date(DayOfWeek.MONDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour tuesday = BusinessHour.builder()
                .date(DayOfWeek.TUESDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour wednesday = BusinessHour.builder()
                .date(DayOfWeek.WEDNESDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour thursday = BusinessHour.builder()
                .date(DayOfWeek.THURSDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour friday = BusinessHour.builder()
                .date(DayOfWeek.FRIDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour saturday = BusinessHour.builder()
                .date(DayOfWeek.SATURDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour sunday = BusinessHour.builder()
                .date(DayOfWeek.SUNDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        Set<BusinessHour> businessHours = new HashSet<>(Set.of(monday, tuesday, wednesday, thursday, friday, saturday, sunday));

        Classification classification = Classification.builder()
                .id(1L)
                .type(ClassificationType.CLASSIFICATION)
                .name("카페")
                .latitude(33.33)
                .longitude(33.33)
                .build();
        Classification subClassification = Classification.builder()
                .id(1L)
                .type(ClassificationType.SUB_CLASSIFICATION)
                .name("카페")
                .build();

        StoreMeta storeMeta = StoreMeta.builder()
                .id(3L)
                .name("스타벅스")
                .deleted(false)
                .activated(true)
                .classification(classification)
                .subClassification(subClassification)
                .category("카페")
                .latitude(37.503716)
                .longitude(127.053718)
                .password("1234")
                .businessHours(businessHours)
                .build();

        StoreDetail forImage = StoreDetail.builder()
                .id(1L)
                .build();

        StoreImage image = StoreImage.builder()
                .id(1L)
                .storeDetail(forImage)
                .imageUrl("first")
                .build();
        StoreImage image2 = StoreImage.builder()
                .id(1L)
                .storeDetail(forImage)
                .imageUrl("first")
                .build();
        StoreImage image3 = StoreImage.builder()
                .id(1L)
                .storeDetail(forImage)
                .imageUrl("first")
                .build();

        Set<StoreImage> images = new HashSet<>(Set.of(image, image2, image3));

        StoreDetail storeDetail = StoreDetail.builder()
                .id(1L)
                .storeMeta(storeMeta)
                .umbrellaLocation("우산 위치")
                .workingHour("영업시간")
                .instaUrl("인스타 주소")
                .contactInfo("연락처")
                .address("주소")
                .addressDetail("상세주소")
                .content("내용")
                .storeImages(images)
                .build();



        @Test
        @DisplayName("썸네일은 이미지들의 첫 번째 사진이다.")
        void thumbnailTest() {
            // given
            SingleStoreResponse singleStoreResponse = new SingleStoreResponse(storeDetail);
            // when


            // then
            singleStoreResponse.getThumbnail().equals("first");

        }

        @Test
        @DisplayName("이미지들은 pk 기준으로 정렬된다.")
        void imageOrderTest() {
            // given
            SingleStoreResponse singleStoreResponse = new SingleStoreResponse(storeDetail);


            // when


            // then
            for (int i = 0; i < singleStoreResponse.getImageUrls().size() - 1; i++) {
                assertTrue(singleStoreResponse.getImageUrls().get(i).getId() <= singleStoreResponse.getImageUrls().get(i + 1).getId());
            }
        }

        @Test
        @DisplayName("영업시간은 요일 순서대로 정렬된다.")
        void dateOrderTest() {
            // given
            SingleStoreResponse singleStoreResponse = new SingleStoreResponse(storeDetail);

            // when


            // then
            for (int i = 0; i < singleStoreResponse.getBusinessHours().size() - 1; i++) {
                assertTrue(singleStoreResponse.getBusinessHours().get(i).getDate().compareTo(singleStoreResponse.getBusinessHours().get(i + 1).getDate()) <= 0);
            }
        }
    }

}
