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
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse;
import upbrella.be.store.entity.StoreMeta;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreMetaServiceTest {

    @Mock
    private StoreMetaRepository storeMetaRepository;

    @InjectMocks
    private StoreMetaService storeMetaService;

    @Test
    void findById() {
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