package upbrella.be.store.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;
import upbrella.be.store.dto.request.UpdateStoreRequest;
import upbrella.be.store.dto.response.*;
import upbrella.be.store.entity.*;
import upbrella.be.store.exception.NonExistingStoreDetailException;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.umbrella.service.UmbrellaService;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreDetailServiceTest {

    @Mock
    private ClassificationService classificationService;
    @Mock
    private StoreMetaService storeMetaService;
    @Mock
    private UmbrellaService umbrellaService;
    @Mock
    private StoreDetailRepository storeDetailRepository;
    @Mock
    private BusinessHourService businessHourService;
    @Mock
    private StoreImageService storeImageService;
    @InjectMocks
    private StoreDetailService storeDetailService;

    @Nested
    @DisplayName("협업 지점의 고유번호를 입력받아")
    class findStoreDetailByStoreMetaIdTest {

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
        List<BusinessHour> businessHours = List.of(monday, tuesday);

        StoreMeta storeMeta = StoreMeta.builder()
                .id(3L)
                .name("스타벅스")
                .deleted(false)
                .latitude(37.503716)
                .longitude(127.053718)
                .activated(true)
                .deleted(false)
                .category("카페, 디저트")
                .businessHours(businessHours)
                .build();

        StoreDetail storeDetail = StoreDetail.builder()
                .id(2L)
                .storeMeta(storeMeta)
                .content("모티브 카페 소개")
                .address("모티브로 32길")
                .contactInfo("010-5252-8282")
                .instaUrl("모티브 인서타")
                .workingHour("매일 7시 ~ 12시")
                .umbrellaLocation("문 앞")
                .storeImages(Set.of())
                .build();

        StoreFindByIdResponse storeFindByIdResponseExpected = StoreFindByIdResponse.fromStoreDetail(storeDetail, 10L);



        @DisplayName("해당하는 협업 지점의 정보를 성공적으로 반환한다.")
        @Test
        void success() {

            // given

            given(storeDetailRepository.findByStoreMetaIdUsingFetchJoin(3L))
                    .willReturn(Optional.of(storeDetail));

            given(umbrellaService.countAvailableUmbrellaAtStore(3L))
                    .willReturn(10L);

            //when
            StoreFindByIdResponse storeFindByIdResponse = storeDetailService.findStoreDetailByStoreId(3L);

            //then
            assertAll(
                    () -> assertThat(storeFindByIdResponse)
                            .usingRecursiveComparison()
                            .isEqualTo(storeFindByIdResponseExpected),
                    () -> then(storeDetailRepository).should(times(1))
                            .findByStoreMetaIdUsingFetchJoin(3L),
                    () -> then(umbrellaService).should(times(1))
                            .countAvailableUmbrellaAtStore(3L)
            );
        }
    }

    @Nested
    @DisplayName("사용자는 ")
    class StoreDetailNestedTest {

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

        Classification classification = Classification.builder()
                .id(1L)
                .type(ClassificationType.CLASSIFICATION)
                .name("대분류")
                .latitude(33.33)
                .longitude(33.33)
                .build();

        Classification subClassification = Classification.builder()
                .id(2L)
                .type(ClassificationType.SUB_CLASSIFICATION)
                .name("소분류")
                .build();

        List<BusinessHour> businessHours = List.of(monday, tuesday, wednesday, thursday, friday, saturday, sunday);


        StoreMeta storeMeta = StoreMeta.builder()
                .id(1L)
                .name("협업 지점명")
                .activated(true)
                .deleted(false)
                .classification(classification)
                .subClassification(subClassification)
                .category("카테고리")
                .latitude(33.33)
                .longitude(33.33)
                .password("비밀번호")
                .businessHours(businessHours)
                .build();

        StoreImage first = StoreImage.builder()
                .id(1L)
                .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
                .build();

        StoreImage second = StoreImage.builder()
                .id(2L)
                .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
                .build();

        Set<StoreImage> images = Set.of(first, second);

        StoreDetail storeDetail = StoreDetail.builder()
                .id(1L)
                .storeMeta(storeMeta)
                .umbrellaLocation("우산 위치")
                .workingHour("근무 시간")
                .instaUrl("인스타그램 주소")
                .contactInfo("연락처")
                .address("주소")
                .addressDetail("상세 주소")
                .content("내용")
                .storeImages(images)
                .build();

        @Test
        @DisplayName("모든 협업 지점의 정보를 조회할 수 있다.")
        void findAllTest() {
            // given
            given(storeDetailRepository.findAllStores()).willReturn(List.of(storeDetail));

            SingleStoreResponse expected = SingleStoreResponse.builder()
                    .id(1L)
                    .name("협업 지점명")
                    .activateStatus(true)
                    .classification(SingleClassificationResponse.builder()
                            .id(1L)
                            .name("대분류")
                            .type(ClassificationType.CLASSIFICATION)
                            .latitude(33.33)
                            .longitude(33.33)
                            .build())
                    .subClassification(SingleSubClassificationResponse.builder()
                            .id(2L)
                            .type(ClassificationType.SUB_CLASSIFICATION)
                            .name("소분류")
                            .build())
                    .category("카테고리")
                    .latitude(33.33)
                    .longitude(33.33)
                    .password("비밀번호")
                    .umbrellaLocation("우산 위치")
                    .businessHour("근무 시간")
                    .instagramId("인스타그램 주소")
                    .contactNumber("연락처")
                    .address("주소")
                    .addressDetail("상세 주소")
                    .content("내용")
                    .build();

            // when
            List<SingleStoreResponse> allStores = storeDetailService.findAllStores();

            // then
            assertAll(
                    () -> assertThat(allStores).hasSize(1),
                    () -> assertThat(allStores.get(0))
                            .usingRecursiveComparison()
                            .isEqualTo(expected)
            );
        }
    }

    @Nested
    @DisplayName("사용자는 협업지점 상세정보를 ")
    class findStoreDetail {
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

        Classification classification = Classification.builder()
                .id(1L)
                .type(ClassificationType.CLASSIFICATION)
                .name("대분류")
                .latitude(33.33)
                .longitude(33.33)
                .build();

        Classification subClassification = Classification.builder()
                .id(2L)
                .type(ClassificationType.SUB_CLASSIFICATION)
                .name("소분류")
                .build();

        List<BusinessHour> businessHours = List.of(monday, tuesday, wednesday, thursday, friday, saturday, sunday);


        StoreMeta storeMeta = StoreMeta.builder()
                .id(1L)
                .name("협업 지점명")
                .activated(true)
                .deleted(false)
                .classification(classification)
                .subClassification(subClassification)
                .category("카테고리")
                .latitude(33.33)
                .longitude(33.33)
                .password("비밀번호")
                .businessHours(businessHours)
                .build();

        StoreImage first = StoreImage.builder()
                .id(1L)
                .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
                .build();

        StoreImage second = StoreImage.builder()
                .id(2L)
                .imageUrl("https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg")
                .build();

        Set<StoreImage> images = Set.of(first, second);

        StoreDetail storeDetail = StoreDetail.builder()
                .id(1L)
                .storeMeta(storeMeta)
                .umbrellaLocation("우산 위치")
                .workingHour("근무 시간")
                .instaUrl("인스타그램 주소")
                .contactInfo("연락처")
                .address("주소")
                .addressDetail("상세 주소")
                .content("내용")
                .storeImages(images)
                .build();

        @Test
        @DisplayName("id로 조회할 수 있다.")
        void findByIdTest() {
            // given
            long storeMetaId = 1L;
            given(storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeMetaId)).willReturn(Optional.of(storeDetail));

            // when
            StoreDetail storeDetailById = storeDetailService.findStoreDetailByStoreMetaId(storeMetaId);

            // then
            assertAll(
                    () -> assertThat(storeDetailById).isNotNull(),
                    () -> assertThat(storeDetailById)
                            .usingRecursiveComparison()
                            .isEqualTo(storeDetail)
            );
        }

        @Test
        @DisplayName("id로 조회하는데 없을 경우 예외를 발생시킨다.")
        void notFoundException() {
            // given
            long storeDetailId = 1L;

            // when


            // then
            assertThatThrownBy(() -> storeDetailService.findStoreDetailByStoreMetaId(storeDetailId))
                    .isInstanceOf(NonExistingStoreDetailException.class)
                    .hasMessageContaining("[ERROR] 존재하지 않는 가게입니다.");
        }
    }

    @Test
    @DisplayName("사용자는 협업지점을 수정할 수 있다.")
    void updateStoreTest() {
        // given

        Long storeId = 1L;

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

        List<BusinessHour> businessHours = List.of(monday, tuesday, wednesday, thursday, friday, saturday, sunday);

        Classification classification = Classification.builder()
                .id(1L)
                .type(ClassificationType.CLASSIFICATION)
                .name("대분류")
                .latitude(33.33)
                .longitude(33.33)
                .build();

        Classification subClassification = Classification.builder()
                .id(2L)
                .type(ClassificationType.SUB_CLASSIFICATION)
                .name("소분류")
                .build();

        StoreMeta storeMeta = StoreMeta.builder()
                .id(1L)
                .name("협업 지점명")
                .activated(true)
                .deleted(false)
                .classification(classification)
                .subClassification(subClassification)
                .category("카테고리")
                .latitude(33.33)
                .longitude(33.33)
                .password("비밀번호")
                .businessHours(businessHours)
                .build();

        StoreDetail storedetail = StoreDetail.builder()
                .id(storeId)
                .storeMeta(storeMeta)
                .umbrellaLocation("우산 위치")
                .workingHour("근무 시간")
                .instaUrl("인스타그램 주소")
                .contactInfo("연락처")
                .address("주소")
                .addressDetail("상세 주소")
                .content("내용")
                .storeImages(Set.of())
                .build();

        // 수정 후

        SingleBusinessHourRequest mondayUpdate = SingleBusinessHourRequest.builder()
                .date(DayOfWeek.MONDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();

        SingleBusinessHourRequest tuesdayUpdate = SingleBusinessHourRequest.builder()
                .date(DayOfWeek.TUESDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();

        SingleBusinessHourRequest wednesdayUpdate = SingleBusinessHourRequest.builder()
                .date(DayOfWeek.WEDNESDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();

        SingleBusinessHourRequest thursdayUpdate = SingleBusinessHourRequest.builder()
                .date(DayOfWeek.THURSDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();

        SingleBusinessHourRequest fridayUpdate = SingleBusinessHourRequest.builder()
                .date(DayOfWeek.FRIDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();

        SingleBusinessHourRequest saturdayUpdate = SingleBusinessHourRequest.builder()
                .date(DayOfWeek.SATURDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();

        SingleBusinessHourRequest sundayUpdate = SingleBusinessHourRequest.builder()
                .date(DayOfWeek.SUNDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();

        List<SingleBusinessHourRequest> businessHoursUpdate = List.of(mondayUpdate, tuesdayUpdate, wednesdayUpdate, thursdayUpdate, fridayUpdate, saturdayUpdate, sundayUpdate);

        Classification classificationUpdate = Classification.builder()
                .id(3L)
                .type(ClassificationType.CLASSIFICATION)
                .name("대분류 수정")
                .latitude(33.33)
                .longitude(33.33)
                .build();

        Classification subClassificationUpdate = Classification.builder()
                .id(4L)
                .type(ClassificationType.SUB_CLASSIFICATION)
                .name("소분류 수정")
                .build();

        UpdateStoreRequest request = UpdateStoreRequest.builder()
                .name("협업 지점명 수정")
                .category("카테고리 수정")
                .classificationId(3L)
                .subClassificationId(4L)
                .address("주소 수정")
                .addressDetail("상세 주소 수정")
                .umbrellaLocation("우산 위치 수정")
                .businessHour("근무 시간 수정")
                .contactNumber("연락처 수정")
                .instagramId("인스타그램 주소 수정")
                .latitude(44.44)
                .longitude(44.44)
                .content("내용 수정")
                .password("비밀번호 수정")
                .businessHours(businessHoursUpdate)
                .build();


        given(storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeId)).willReturn(Optional.of(storedetail));
        given(classificationService.findClassificationById(request.getClassificationId())).willReturn(classificationUpdate);
        given(classificationService.findSubClassificationById(request.getSubClassificationId())).willReturn(subClassificationUpdate);
        given(storeMetaService.findStoreMetaById(storeId)).willReturn(storeMeta);


        // when
        storeDetailService.updateStore(storeId, request);

        // then
        StoreMeta foundStoreMeta = storeMetaService.findStoreMetaById(storeId);


        StoreDetail foundStoreDetail = storeDetailService.findStoreDetailByStoreMetaId(storeId);

        assertAll(
                () -> assertThat(foundStoreMeta) // 업데이트된 필드에 대한 검증
                        .hasFieldOrPropertyWithValue("classification", classificationUpdate)
                        .hasFieldOrPropertyWithValue("subClassification", subClassificationUpdate)
                        .hasFieldOrPropertyWithValue("category", request.getCategory())
                        .hasFieldOrPropertyWithValue("latitude", request.getLatitude())
                        .hasFieldOrPropertyWithValue("longitude", request.getLongitude())
                        .hasFieldOrPropertyWithValue("category", request.getCategory())
                        .hasFieldOrPropertyWithValue("password", request.getPassword()),

                () -> assertThat(foundStoreDetail)
                        .hasFieldOrPropertyWithValue("umbrellaLocation", request.getUmbrellaLocation())
                        .hasFieldOrPropertyWithValue("workingHour", request.getBusinessHour())
                        .hasFieldOrPropertyWithValue("instaUrl", request.getInstagramId())
                        .hasFieldOrPropertyWithValue("contactInfo", request.getContactNumber())
                        .hasFieldOrPropertyWithValue("address", request.getAddress())
                        .hasFieldOrPropertyWithValue("addressDetail", request.getAddressDetail())
                        .hasFieldOrPropertyWithValue("content", request.getContent())
        );
    }

    @Test
    @DisplayName("사용자는 협업지점 소개 페이지를 조회할 수 있다.")
    void findStoreIntroductionTest() {
        // given
        StoreMeta storeMeta = StoreMeta.builder()
                .id(3L)
                .name("스타벅스")
                .deleted(false)
                .latitude(37.503716)
                .longitude(127.053718)
                .activated(true)
                .deleted(false)
                .category("카페, 디저트")
                .subClassification(Classification.builder().id(1L).name("카페, 디저").type(ClassificationType.SUB_CLASSIFICATION).build())
                .build();

        StoreDetail storeDetail = StoreDetail.builder()
                .id(2L)
                .storeMeta(storeMeta)
                .content("모티브 카페 소개")
                .address("모티브로 32길")
                .contactInfo("010-5252-8282")
                .instaUrl("모티브 인서타")
                .workingHour("매일 7시 ~ 12시")
                .umbrellaLocation("문 앞")
                .storeImages(Set.of(StoreImage.builder().imageUrl("가게 썸네일").build()))
                .build();

        StoreIntroductionsResponseByClassification storeIntroductionsResponseByClassification = StoreIntroductionsResponseByClassification
                .builder()
                .subClassificationId(1)
                .stores(List.of(SingleStoreIntroductionResponse.of(3L, "가게 썸네일", "스타벅스", "카페, 디저트")))
                .build();

        AllStoreIntroductionResponse expected = AllStoreIntroductionResponse.builder()
                .storesByClassification(List.of(storeIntroductionsResponseByClassification))
                .build();

        given(storeDetailRepository.findAllStores()).willReturn(List.of(storeDetail));

        // when
        AllStoreIntroductionResponse response = storeDetailService.findAllStoreIntroductions();

        // then
        assertAll(
                () -> assertThat(response)
                        .usingRecursiveComparison()
                        .isEqualTo(expected)
        );
    }
}
