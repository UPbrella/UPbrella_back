package upbrella.be.store.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

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
}