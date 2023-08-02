package upbrella.be.store.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.dto.response.StoreFindByIdResponse;
import upbrella.be.store.entity.*;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.umbrella.service.UmbrellaService;

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
    private UmbrellaService umbrellaService;
    @Mock
    private StoreDetailRepository storeDetailRepository;
    @InjectMocks
    private StoreDetailService storeDetailService;

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
}
