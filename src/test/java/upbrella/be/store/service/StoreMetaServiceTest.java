package upbrella.be.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.dto.request.CoordinateRequest;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreImage;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.repository.ClassificationRepository;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.store.repository.StoreImageRepository;
import upbrella.be.store.repository.StoreMetaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreMetaServiceTest {

    @Mock
    private StoreMetaRepository storeMetaRepository;

    @Mock
    private StoreDetailRepository storeDetailRepository;

    @Mock
    private StoreImageRepository storeImageRepository;
    @Mock
    private ClassificationRepository classificationRepository;

    @InjectMocks
    private StoreMetaService storeMetaService;

    @Test
    @DisplayName("존재하는 협업지점을 조회시 정상 조회된다.")
    void findByIdTest() {
        StoreMeta storeMeta = StoreMeta.builder().build();
        given(storeMetaRepository.findByIdAndDeletedIsFalse(1L)).willReturn(Optional.of(storeMeta));

        storeMetaService.findStoreMetaById(1L);

        verify(storeMetaRepository, times(1)).findByIdAndDeletedIsFalse(1L);
    }


    @Test
    @DisplayName("협업지점 생성시 정상적으로 생성된다.")
    void createStoreTest() {
        // given
        CreateStoreRequest request = CreateStoreRequest.builder()
                .name("업브렐라")
                .category("카페")
                .classificationId(3L)
                .subClassificationId(4L)
                .activateStatus(true)
                .address("서울특별시 강남구 테헤란로 427")
                .umbrellaLocation("1층")
                .businessHours("09:00 ~ 18:00")
                .contactNumber("010-0000-0000")
                .instagramId("upbrella")
                .latitude(37.503716)
                .longitude(127.053718)
                .content("업브렐라입니다.")
                .imageUrls(
                        List.of(
                                "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/1.jpg",
                                "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/2.jpg",
                                "https://upbrella.s3.ap-northeast-2.amazonaws.com/umbrella-store/1/3.jpg"
                        )
                )
                .build();

        given(storeMetaRepository.save(any(StoreMeta.class))).willReturn(StoreMeta.builder().build());
        given(storeDetailRepository.save(any(StoreDetail.class))).willReturn(StoreDetail.builder().build());
        given(storeImageRepository.save(any(StoreImage.class))).willReturn(StoreImage.builder().build());
        given(classificationRepository.findById(anyLong())).willReturn(Optional.of(Classification.builder().build()));

        storeMetaService.createStore(request);

        verify(storeMetaRepository, times(1)).save(any(StoreMeta.class));
        verify(storeDetailRepository, times(1)).save(any(StoreDetail.class));
        verify(storeImageRepository, times(request.getImageUrls().size())).save(any(StoreImage.class));
    }

    @Test
    @DisplayName("협업지점을 삭제할 수 있다.")
    void StoreMetaDeleteTest() {
        // given
        long storeId = 1L;
        StoreMeta storeMeta = mock(StoreMeta.class);

        given(storeMetaRepository.findById(storeId)).willReturn(Optional.of(storeMeta));

        // when
        storeMetaService.deleteStoreMeta(storeId);

        // then
        verify(storeMeta, times(1)).delete();
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

