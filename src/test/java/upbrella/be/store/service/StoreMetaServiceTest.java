package upbrella.be.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.dto.request.CoordinateRequest;
import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse;
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class StoreMetaServiceTest {

    @Mock
    private UmbrellaRepository umbrellaRepository;

    @Mock
    private StoreMetaRepository storeMetaRepository;

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
                    .thumbnail("star")
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
                    .thumbnail("star")
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
                            .isInstanceOf(IllegalArgumentException.class),
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
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(umbrellaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(2L)
            );
        }
    }

    @Nested
    @DisplayName("현재 지도 상에 보이는 위도, 경도 범위를 입력받아")
    class findStoresInCurrentMapTest {
        private List<StoreMeta> storeMetaList = new ArrayList<>();

        private SingleCurrentLocationStoreResponse response;

        @BeforeEach
        void setUp() {
            StoreMeta storeIn = StoreMeta.builder()
                    .id(1)
                    .thumbnail("사진1")
                    .name("모티브 카페 신촌 지점")
                    .activated(true)
                    .deleted(false)
                    .latitude(4)
                    .longitude(3)
                    .build();

            StoreMeta storeOut = StoreMeta.builder()
                    .id(1)
                    .thumbnail("사진2")
                    .name("모티브 카페 미국 지점")
                    .activated(true)
                    .deleted(false)
                    .latitude(6)
                    .longitude(3)
                    .build();

            response = SingleCurrentLocationStoreResponse.builder()
                    .id(1)
                    .latitude(4)
                    .longitude(3)
                    .name("모티브 카페 신촌 지점")
                    .openStatus(true)
                    .build();

            storeMetaList.add(storeIn);
        }

        @Test
        @DisplayName("지도 상의 협업 지점 정보를 성공적으로 반환한다.")
        void success() {

            //given
            given(storeMetaRepository.findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(3.0, 5.0, 2.0, 4.0))
                    .willReturn(storeMetaList);

            //when
            List<SingleCurrentLocationStoreResponse> storesInCurrentMap = storeMetaService.findStoresInCurrentMap(new CoordinateRequest(3.0, 5.0, 2.0, 4.0));

            //then
            assertAll(
                    () -> assertThat(storesInCurrentMap.size())
                            .isEqualTo(1),
                    () -> assertThat(storesInCurrentMap.get(0))
                            .usingRecursiveComparison()
                            .isEqualTo(response)
            );
        }

        @Test
        @DisplayName("만족하는 협업 지점이 없으면 빈 리스트를 반환한다.")
        void empty() {

            //given
            given(storeMetaRepository.findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(3.0, 5.0, 2.0, 4.0))
                    .willReturn(List.of());

            //when
            List<SingleCurrentLocationStoreResponse> storesInCurrentMap = storeMetaService.findStoresInCurrentMap(new CoordinateRequest(3.0, 5.0, 2.0, 4.0));

            //then
            assertThat(storesInCurrentMap.size())
                    .isEqualTo(0);
        }
    }

}