package upbrella.be.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.dto.request.CoordinateRequest;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;
import upbrella.be.store.dto.response.AllCurrentLocationStoreResponse;
import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse;
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse;
import upbrella.be.store.entity.*;
import upbrella.be.store.exception.DeletedStoreDetailException;
import upbrella.be.store.exception.NonExistingStoreMetaException;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.store.repository.StoreMetaRepository;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.exception.NonExistingUmbrellaException;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StoreMetaServiceTest {

    @Mock
    private UmbrellaRepository umbrellaRepository;
    @Mock
    private StoreMetaRepository storeMetaRepository;
    @Mock
    private StoreDetailRepository storeDetailRepository;
    @Mock
    private ClassificationService classificationService;
    @Mock
    private BusinessHourService businessHourService;
    @InjectMocks
    private StoreMetaService storeMetaService;

    @Nested
    @DisplayName("우산의 고유번호를 입력받아")
    class findCurrentStoreIdByUmbrellaTest {

        @DisplayName("해당하는 우산이 보관된 협업 지점의 고유번호, 가게 이름 정보를 성공적으로 반환한다.")
        @Test
        void success() {

            //given
            StoreMeta storeMeta = StoreMeta.builder()
                    .id(3L)
                    .name("스타벅스")
                    .deleted(false)
                    .build();

            Umbrella umbrella = Umbrella.builder()
                    .id(2L)
                    .uuid(45L)
                    .deleted(false)
                    .rentable(true)
                    .storeMeta(storeMeta)
                    .build();

            given(umbrellaRepository.findByIdAndDeletedIsFalse(2L))
                    .willReturn(Optional.of(umbrella));

            //when
            CurrentUmbrellaStoreResponse currentStoreIdByUmbrella = storeMetaService.findCurrentStoreIdByUmbrella(2L);

            //then
            assertAll(
                    () -> assertThat(currentStoreIdByUmbrella.getId())
                            .isEqualTo(3L),
                    () -> assertThat(currentStoreIdByUmbrella.getName())
                            .isEqualTo("스타벅스"),
                    () -> then(umbrellaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(2L)
            );
        }

        @DisplayName("해당하는 우산이 보관된 협업 지점이 삭제된 상태면 예외를 반환시킨다.")
        @Test
        void isAtDeletedStore() {

            //given
            StoreMeta storeMeta = StoreMeta.builder()
                    .id(3L)
                    .name("스타벅스")
                    .deleted(true)
                    .build();

            Umbrella umbrella = Umbrella.builder()
                    .id(2L)
                    .uuid(45L)
                    .deleted(false)
                    .rentable(true)
                    .storeMeta(storeMeta)
                    .build();

            given(umbrellaRepository.findByIdAndDeletedIsFalse(2L))
                    .willReturn(Optional.of(umbrella));

            //then
            assertAll(
                    () -> assertThatThrownBy(() -> storeMetaService.findCurrentStoreIdByUmbrella(2L))
                            .isInstanceOf(DeletedStoreDetailException.class),
                    () -> then(umbrellaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(2L)
            );
        }

        @DisplayName("해당하는 우산이 존재하지 않으면 예외를 반환시킨다.")
        @Test
        void isNonExistingUmbrella() {

            //given
            given(umbrellaRepository.findByIdAndDeletedIsFalse(2L))
                    .willReturn(Optional.ofNullable(null));

            //then
            assertAll(
                    () -> assertThatThrownBy(() -> storeMetaService.findCurrentStoreIdByUmbrella(2L))
                            .isInstanceOf(NonExistingUmbrellaException.class),
                    () -> then(umbrellaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(2L)
            );
        }
    }

    @Nested
    @DisplayName("현재 지도 상에 보이는 위도, 경도와 현재 시각을 입력받아")
    class findStoresInCurrentMapTest {
        private List<StoreMeta> storeMetaList = new ArrayList<>();

        private SingleCurrentLocationStoreResponse expected;
        private Set<BusinessHour> businessHours;

        @BeforeEach
        void setUp() {

            businessHours = Set.of(
                    BusinessHour.builder()
                            .id(1L)
                            .date(DayOfWeek.MONDAY)
                            .openAt(LocalTime.NOON)
                            .closeAt(LocalTime.of(23, 0))
                            .build(),
                    BusinessHour.builder()
                            .id(2L)
                            .date(DayOfWeek.TUESDAY)
                            .openAt(LocalTime.NOON)
                            .closeAt(LocalTime.of(23, 0))
                            .build(),
                    BusinessHour.builder()
                            .id(3L)
                            .date(DayOfWeek.WEDNESDAY)
                            .openAt(LocalTime.NOON)
                            .closeAt(LocalTime.of(23, 0))
                            .build(),
                    BusinessHour.builder()
                            .id(4L)
                            .date(DayOfWeek.THURSDAY)
                            .openAt(LocalTime.NOON)
                            .closeAt(LocalTime.of(23, 0))
                            .build(),
                    BusinessHour.builder()
                            .id(5L)
                            .date(DayOfWeek.FRIDAY)
                            .openAt(LocalTime.NOON)
                            .closeAt(LocalTime.of(23, 0))
                            .build());

            StoreMeta storeIn = StoreMeta.builder()
                    .id(1)
                    .name("모티브 카페 신촌 지점")
                    .activated(true)
                    .deleted(false)
                    .latitude(4)
                    .longitude(3)
                    .businessHours(businessHours)
                    .build();

            StoreMeta storeOff = StoreMeta.builder()
                    .id(1)
                    .name("모티브 카페 공사 중")
                    .activated(false)
                    .deleted(false)
                    .latitude(4)
                    .longitude(3)
                    .businessHours(businessHours)
                    .build();

            expected = SingleCurrentLocationStoreResponse.builder()
                    .id(1)
                    .latitude(4)
                    .longitude(3)
                    .name("모티브 카페 신촌 지점")
                    .openStatus(true)
                    .build();

            storeMetaList.add(storeIn);
            storeMetaList.add(storeOff);
        }

        @Test
        @DisplayName("지도 상의 협업 지점과 현재 시각을 토대로 영업 여부를 판단하여 정보를 반환한다.")
        void success() {

            //given
            given(storeMetaRepository.findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(3.0, 5.0, 2.0, 4.0))
                    .willReturn(storeMetaList);

            //when
            AllCurrentLocationStoreResponse storesInCurrentMap = storeMetaService.findStoresInCurrentMap(new CoordinateRequest(3.0, 5.0, 2.0, 4.0), LocalDateTime.of(2023, 8, 4, 13, 0));

            //then
            assertAll(
                    () -> assertThat(storesInCurrentMap.getStores().size())
                            .isEqualTo(2),
                    () -> assertThat(storesInCurrentMap.getStores().get(0))
                            .usingRecursiveComparison()
                            .isEqualTo(expected)
            );
        }

        @Test
        @DisplayName("내부 공사 등을 이유로 비활성화 상태인 협업 지점은 영업 시간이더라도 영업 중으로 표시되지 않는다.")
        void isNotActiveStore() {

            //given
            given(storeMetaRepository.findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(3.0, 5.0, 2.0, 4.0))
                    .willReturn(storeMetaList);

            //when
            AllCurrentLocationStoreResponse storesInCurrentMap = storeMetaService.findStoresInCurrentMap(new CoordinateRequest(3.0, 5.0, 2.0, 4.0), LocalDateTime.of(2023, 8, 4, 13, 0));

            //then
            assertAll(
                    () -> assertThat(storesInCurrentMap.getStores().size())
                            .isEqualTo(2),
                    () -> assertThat(storesInCurrentMap.getStores().get(1).isOpenStatus())
                            .isEqualTo(false)
            );
        }

        @Test
        @DisplayName("영업 시간이 아닌 협업 지점은 영업 중으로 표시되지 않는다.")
        void isNotOpen() {

            //given
            given(storeMetaRepository.findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(3.0, 5.0, 2.0, 4.0))
                    .willReturn(storeMetaList);

            //when
            AllCurrentLocationStoreResponse storesInCurrentMap = storeMetaService.findStoresInCurrentMap(new CoordinateRequest(3.0, 5.0, 2.0, 4.0), LocalDateTime.of(2023, 8, 4, 3, 0));

            //then
            assertAll(
                    () -> assertThat(storesInCurrentMap.getStores().size())
                            .isEqualTo(2),
                    () -> assertThat(storesInCurrentMap.getStores().get(0).isOpenStatus())
                            .isEqualTo(false)
            );
        }


        @Test
        @DisplayName("만족하는 협업 지점이 없으면 빈 리스트를 반환한다.")
        void empty() {

            //given
            given(storeMetaRepository.findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(3.0, 5.0, 2.0, 4.0))
                    .willReturn(List.of());

            //when
            AllCurrentLocationStoreResponse storesInCurrentMap = storeMetaService.findStoresInCurrentMap(new CoordinateRequest(3.0, 5.0, 2.0, 4.0), LocalDateTime.of(1995, 7, 18, 13, 0));

            //then
            assertThat(storesInCurrentMap.getStores().size())
                    .isEqualTo(0);
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
                    .type(ClassificationType.CLASSIFICATION)
                    .name("카테고리")
                    .latitude(33.33)
                    .longitude(33.33)
                    .build();

            Classification subClassification = Classification.builder()
                    .id(classificationId)
                    .type(ClassificationType.SUB_CLASSIFICATION)
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

            StoreDetail storeDetail = StoreDetail.createForSave(store, storeMeta);

            given(classificationService.findClassificationById(classificationId)).willReturn(classification);
            given(classificationService.findSubClassificationById(subClassificationId)).willReturn(subClassification);
            given(storeMetaRepository.save(any(StoreMeta.class))).willReturn(storeMeta);
            given(storeDetailRepository.save(any(StoreDetail.class))).willReturn(storeDetail);
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

    @Test
    @DisplayName("협업지점 삭제 테스트")
    void deleteStoreMetaTest() {

        // given
        Classification classification = Classification.builder()
                .id(1L)
                .type(ClassificationType.CLASSIFICATION)
                .name("카테고리")
                .latitude(33.33)
                .longitude(33.33)
                .build();

        Classification subClassification = Classification.builder()
                .id(2L)
                .type(ClassificationType.SUB_CLASSIFICATION)
                .name("카테고리")
                .build();

        BusinessHour businessHour = BusinessHour.builder()
                .id(1L)
                .date(DayOfWeek.MONDAY)
                .openAt(LocalTime.of(10, 0))
                .closeAt(LocalTime.of(20, 0))
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
                .businessHours(Set.of(businessHour))
                .build();

        given(storeMetaRepository.findById(1L)).willReturn(Optional.of(storeMeta));

        // when
        storeMetaService.deleteStoreMeta(1L);

        // then
        assertAll(
                () -> verify(storeMetaRepository, times(1)).findById(1L),
                () -> assertThat(storeMeta.isDeleted()).isTrue()
        );

    }

    @Nested
    @DisplayName("사용자는 ")
    class findStoreMeta {

        @Test
        @DisplayName("협업지점을 고유 아이디로 조회할 수 있다.")
        void test() {

            // given
            Classification classification = Classification.builder()
                    .id(1L)
                    .type(ClassificationType.CLASSIFICATION)
                    .name("카테고리")
                    .latitude(33.33)
                    .longitude(33.33)
                    .build();

            Classification subClassification = Classification.builder()
                    .id(2L)
                    .type(ClassificationType.SUB_CLASSIFICATION)
                    .name("카테고리")
                    .build();

            BusinessHour businessHour = BusinessHour.builder()
                    .id(1L)
                    .date(DayOfWeek.MONDAY)
                    .openAt(LocalTime.of(10, 0))
                    .closeAt(LocalTime.of(20, 0))
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
                    .businessHours(Set.of(businessHour))
                    .build();

            given(storeMetaRepository.findById(1L)).willReturn(Optional.of(storeMeta));

            // when
            StoreMeta foundStoreMeta = storeMetaService.findStoreMetaById(1L);

            // then
            assertAll(
                    () -> verify(storeMetaRepository, times(1)).findById(1L),
                    () -> assertThat(foundStoreMeta).isEqualTo(storeMeta)
            );
        }

        @Test
        @DisplayName("협업지점이 존재하지 않다면 예외가 발생한다.")
        void storeMetaNotFoundTest() {

            // given

            given(storeMetaRepository.findById(1L)).willReturn(Optional.empty());

            // when

            // then
            assertThatThrownBy(() -> storeMetaService.findStoreMetaById(1L))
                    .isInstanceOf(NonExistingStoreMetaException.class)
                    .hasMessage("[ERROR] 존재하지 않는 협업 지점 고유번호입니다.");
        }
    }
}