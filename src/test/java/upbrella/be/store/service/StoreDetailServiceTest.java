package upbrella.be.store.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;
import upbrella.be.store.dto.response.StoreFindByIdResponse;
import upbrella.be.store.entity.*;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.store.repository.StoreMetaRepository;
import upbrella.be.umbrella.service.UmbrellaService;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreDetailServiceTest {

    @Mock
    private UmbrellaService umbrellaService;
    @Mock
    private StoreDetailRepository storeDetailRepository;
    @InjectMocks
    private StoreDetailService storeDetailService;
    @Mock
    private ClassificationService classificationService;
    @Mock
    private StoreMetaRepository storeMetaRepository;
    @Mock
    private BusinessHourService businessHourService;
    @InjectMocks
    private StoreMetaService storeMetaService;

    @Nested
    @DisplayName("협업 지점의 고유번호를 입력받아")
    class findStoreDetailByStoreMetaIdTest {

        StoreMeta storeMeta = StoreMeta.builder()
                .id(3L)
                .name("스타벅스")
                .deleted(false)
                .latitude(37.503716)
                .longitude(127.053718)
                .activated(true)
                .deleted(false)
                .category("카페, 디저트")
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

        StoreFindByIdResponse storeFindByIdResponseExpected = StoreFindByIdResponse.builder()
                .id(2L)
                .name("스타벅스")
                .businessHours("매일 7시 ~ 12시")
                .contactNumber("010-5252-8282")
                .address("모티브로 32길")
                .availableUmbrellaCount(10)
                .openStatus(true)
                .latitude(37.503716)
                .longitude(127.053718)
                .build();

        @DisplayName("해당하는 협업 지점의 정보를 성공적으로 반환한다.")
        @Test
        void success() {

            //given
            given(storeDetailRepository.findByStoreMetaIdUsingFetchJoin(3L))
                    .willReturn(Optional.of(storeDetail));

            given(umbrellaService.countAvailableUmbrellaAtStore(3L))
                    .willReturn(10);

            //when
            StoreFindByIdResponse storeFindByIdResponse = storeDetailService.findStoreDetailByStoreMetaId(3L);

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

        @DisplayName("해당하는 협업 지점이 존재하지 않거나 삭제되었으면 예외를 발생시킨다.")
        @Test
        void isNotExistingStore() {

            //given
            given(storeDetailRepository.findByStoreMetaIdUsingFetchJoin(3L))
                    .willReturn(Optional.ofNullable(null));

            //when & then
            assertAll(
                    () -> assertThatThrownBy(() -> storeDetailService.findStoreDetailByStoreMetaId(3L))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(storeDetailRepository).should(times(1))
                            .findByStoreMetaIdUsingFetchJoin(3L),
                    () -> then(umbrellaService).shouldHaveNoInteractions()
            );
        }
    }

    @Nested
    @DisplayName("협업지점 생성 위해 협업지점 정보를 입력받아")
    class createStoreTest {

        CreateStoreRequest store = CreateStoreRequest.builder()
                .name("협업 지점명")
                .category("카테고리")
                .classificationId(1L)
                .subClassificationId(2L)
                .activateStatus(true)
                .address("주소")
                .addressDetail("상세주소")
                .umbrellaLocation("우산 위치")
                .businessHour("영업 시간")
                .contactNumber("연락처")
                .instagramId("인스타그램 아이디")
                .latitude(33.33)
                .longitude(33.33)
                .content("내용")
                .password("비밀번호")
                .businessHours(
                        List.of(
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.MONDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.TUESDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.WEDNESDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.THURSDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.FRIDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.SATURDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.SUNDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build()))
                .build();

        @Test
        @DisplayName("새로운 협업지점을 생성할 수 있다.")
        void createNewStoreTest() {
            // given
            long classificationId = 1L;
            long subClassificationId = 2L;
            Classification classification = Classification.builder()
                    .id(classificationId)
                    .type("classification")
                    .name("카테고리")
                    .latitude(33.33)
                    .longitude(33.33)
                    .build();

            Classification subClassification = Classification.builder()
                    .id(classificationId)
                    .type("classification")
                    .name("카테고리")
                    .build();

            StoreMeta storeMeta = StoreMeta.builder()
                    .name(store.getName())
                    .activated(store.isActivateStatus())
                    .deleted(false)
                    .classification(classification)
                    .subClassification(subClassification)
                    .category(store.getCategory())
                    .latitude(store.getLatitude())
                    .longitude(store.getLongitude())
                    .password(store.getPassword())
                    .build();

            Mockito.when(classificationService.findClassificationById(classificationId)).thenReturn(classification);
            Mockito.when(classificationService.findSubClassificationById(subClassificationId)).thenReturn(subClassification);
            Mockito.when(storeMetaRepository.save(Mockito.any(StoreMeta.class))).thenReturn(storeMeta);
            doNothing().when(businessHourService).saveAllBusinessHour(any());

            // when
            storeMetaService.createStore(store);

            // then
            assertAll(
                    () -> verify(classificationService).findClassificationById(classificationId),
                    () -> verify(classificationService).findSubClassificationById(subClassificationId),
                    () -> verify(storeMetaRepository).save(Mockito.any(StoreMeta.class))
            );
        }
    }
}
