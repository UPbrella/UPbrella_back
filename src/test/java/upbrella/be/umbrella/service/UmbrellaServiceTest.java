package upbrella.be.umbrella.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UmbrellaServiceTest {
    private final UmbrellaRepository umbrellaRepository = mock(UmbrellaRepository.class);
    private final StoreMetaRepository storeMetaRepository = mock(StoreMetaRepository.class);
    private final UmbrellaService umbrellaService = new UmbrellaService(umbrellaRepository, storeMetaRepository);

    @Test
    void findAllUmbrellas() {
    }

    @Test
    void findUmbrellasByStoreId() {
    }

    @Nested
    @DisplayName("우산 추가 API를 호출하면")
    class addUmbrella {

        @Test
        @DisplayName("우산을 정상적으로 추가한다.")
        void success() {
            // given
            UmbrellaRequest umbrellaRequest = UmbrellaRequest.builder()
                    .uuid(43L)
                    .storeMetaId(2L)
                    .rentable(true)
                    .build();

            StoreMeta foundStoreMeta = StoreMeta.builder()
                    .id(2L)
                    .name("name")
                    .thumbnail("thumb")
                    .deleted(true)
                    .build();

            Umbrella umbrella = Umbrella.builder()
                    .id(null)
                    .uuid(43L)
                    .deleted(false)
                    .storeMeta(foundStoreMeta)
                    .rentable(true)
                    .build();

            // when
            given(storeMetaRepository.findById(2L))
                    .willReturn(Optional.of(foundStoreMeta));
            given(umbrellaRepository.existsByUuid(43))
                    .willReturn(false);
            given(umbrellaRepository.save(any(Umbrella.class)))
                    .willReturn(umbrella);

            Umbrella addedUmbrella = umbrellaService.addUmbrella(umbrellaRequest);

            // then
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(addedUmbrella.getUuid())
                    .isEqualTo(43);
            softly.assertThat(addedUmbrella.getStoreMeta().getId())
                    .isEqualTo(2L);
            softly.assertThat(addedUmbrella.getStoreMeta().getName())
                    .isEqualTo("name");
            softly.assertThat(addedUmbrella.getStoreMeta().getThumbnail())
                    .isEqualTo("thumb");
            softly.assertThat(addedUmbrella.getStoreMeta().isDeleted())
                    .isEqualTo(true);
            softly.assertThat(addedUmbrella.isRentable())
                    .isEqualTo(true);
            softly.assertThat(addedUmbrella.isDeleted())
                    .isEqualTo(false);
            softly.assertAll();

            verify(umbrellaRepository, times(1))
                    .existsByUuid(43);
            verify(storeMetaRepository, times(1))
                    .findById(2L);
            verify(umbrellaRepository, times(1))
                    .save(any(Umbrella.class));
        }

        @Test
        @DisplayName("우산 고유번호가 이미 존재하는 경우 예외를 발생시킨다.")
        void withSameId() {
            // given
            UmbrellaRequest umbrellaRequest = UmbrellaRequest.builder()
                    .uuid(43)
                    .storeMetaId(2)
                    .rentable(true)
                    .build();

            StoreMeta foundStoreMeta = StoreMeta.builder()
                    .id(2L)
                    .name("name")
                    .thumbnail("thume")
                    .deleted(true)
                    .build();

            given(storeMetaRepository.findById(2L))
                    .willReturn(Optional.of(foundStoreMeta));
            given(umbrellaRepository.existsByUuid(43))
                    .willReturn(true);
            // when

            assertThatThrownBy(() -> umbrellaService.addUmbrella(umbrellaRequest))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(storeMetaRepository, times(1))
                    .findById(2L);
            verify(umbrellaRepository, times(1))
                    .existsByUuid(43);
            verify(umbrellaRepository, never())
                    .save(any(Umbrella.class));
        }

        @Test
        @DisplayName("추가하려고 하는 가게 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        void atNonExistingStore() {
            // given
            UmbrellaRequest umbrellaRequest = UmbrellaRequest.builder()
                    .uuid(43)
                    .storeMetaId(2)
                    .rentable(true)
                    .build();

            given(storeMetaRepository.findById(2L))
                    .willReturn(Optional.ofNullable(null));
            // when
            assertThatThrownBy(() -> umbrellaService.addUmbrella(umbrellaRequest))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(storeMetaRepository, times(1))
                    .findById(2L);
            verifyNoInteractions(umbrellaRepository);
        }
    }

    @Test
    void modifyUmbrella() {
    }

    @Test
    void deleteUmbrella() {
    }
}