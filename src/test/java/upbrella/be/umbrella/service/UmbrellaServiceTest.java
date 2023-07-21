package upbrella.be.umbrella.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UmbrellaServiceTest {
    @Mock
    private UmbrellaRepository umbrellaRepository;
    @Mock
    private StoreMetaService storeMetaService;
    @InjectMocks
    private UmbrellaService umbrellaService;

    @Test
    void findAllUmbrellas() {
    }

    @Test
    void findUmbrellasByStoreId() {
    }

    @Nested
    @DisplayName("우산의 고유번호, 협력 지점 고유번호, 대여 여부를 입력받아")
    class addUmbrellaTest {
        private UmbrellaRequest umbrellaRequest;
        private StoreMeta foundStoreMeta;
        private Umbrella umbrella;

        @BeforeEach
        void setUp() {
            umbrellaRequest = UmbrellaRequest.builder()
                    .uuid(43L)
                    .storeMetaId(2L)
                    .rentable(true)
                    .build();

            foundStoreMeta = StoreMeta.builder()
                    .id(2L)
                    .name("name")
                    .thumbnail("thumb")
                    .deleted(false)
                    .build();

            umbrella = Umbrella.builder()
                    .uuid(43L)
                    .deleted(false)
                    .storeMeta(foundStoreMeta)
                    .rentable(true)
                    .build();
        }

        @Test
        @DisplayName("우산을 정상적으로 추가할 수 있다.")
        void success() {

            // given
            given(storeMetaService.findById(2L))
                    .willReturn(foundStoreMeta);
            given(umbrellaRepository.existsByUuid(43))
                    .willReturn(false);
            given(umbrellaRepository.save(any(Umbrella.class)))
                    .willReturn(umbrella);

            // when
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
                    .isEqualTo(false);
            softly.assertThat(addedUmbrella.isRentable())
                    .isEqualTo(true);
            softly.assertThat(addedUmbrella.isDeleted())
                    .isEqualTo(false);
            softly.assertAll();

            assertAll(
                    () -> then(umbrellaRepository).should(times(1))
                    .existsByUuid(43),
                    () -> then(storeMetaService).should(times(1))
                    .findById(2L),
                    () -> then(umbrellaRepository).should(times(1))
                    .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("우산 고유번호가 이미 존재하는 경우 예외를 발생시킨다.")
        void withSameId() {

            // given
            given(storeMetaService.findById(2L))
                    .willReturn(foundStoreMeta);
            given(umbrellaRepository.existsByUuid(43))
                    .willReturn(true);
            // when
            assertThatThrownBy(() -> umbrellaService.addUmbrella(umbrellaRequest))
                    .isInstanceOf(IllegalArgumentException.class);

            // then
            assertAll(
                    () -> then(storeMetaService).should(times(1))
                            .findById(2L),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByUuid(43),
                    () -> then(umbrellaRepository).should(never())
                            .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("추가하려고 하는 가게 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        void atNonExistingStore() {

            // given
            given(storeMetaService.findById(2L))
                    .willThrow(new IllegalArgumentException());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> umbrellaService.addUmbrella(umbrellaRequest))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(storeMetaService).should(times(1))
                            .findById(2L),
                    () -> then(umbrellaRepository).shouldHaveNoInteractions()
            );
        }
    }

    @Nested
    @DisplayName("우산의 고유번호, 협력 지점 고유번호, 대여 여부를 입력받아")
    class modifyUmbrellaTest {
        private UmbrellaRequest umbrellaRequest;
        private StoreMeta foundStoreMeta;
        private Umbrella umbrella;

        @BeforeEach
        void setUp() {

            umbrellaRequest = UmbrellaRequest.builder()
                    .uuid(50L)
                    .storeMetaId(5L)
                    .rentable(true)
                    .build();

            foundStoreMeta = StoreMeta.builder()
                    .id(5L)
                    .name("연세대학교 파스쿠치")
                    .thumbnail("정면사진.jpg")
                    .deleted(false)
                    .build();

            umbrella = Umbrella.builder()
                    .id(1L)
                    .uuid(50L)
                    .deleted(false)
                    .storeMeta(foundStoreMeta)
                    .rentable(true)
                    .build();
        }

        @Test
        @DisplayName("우산을 정상적으로 수정한다.")
        void success() {

            // given
            given(storeMetaService.findById(5L))
                    .willReturn(foundStoreMeta);
            given(umbrellaRepository.existsById(1L))
                    .willReturn(true);
            given(umbrellaRepository.existsByUuid(50L))
                    .willReturn(false);
            given(umbrellaRepository.save(any(Umbrella.class)))
                    .willReturn(umbrella);

            // when
            Umbrella modifiedUmbrella = umbrellaService.modifyUmbrella(1L, umbrellaRequest);

            // then
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(modifiedUmbrella.getUuid())
                    .isEqualTo(50L);
            softly.assertThat(modifiedUmbrella.getStoreMeta().getId())
                    .isEqualTo(5L);
            softly.assertThat(modifiedUmbrella.getStoreMeta().getName())
                    .isEqualTo("연세대학교 파스쿠치");
            softly.assertThat(modifiedUmbrella.getStoreMeta().getThumbnail())
                    .isEqualTo("정면사진.jpg");
            softly.assertThat(modifiedUmbrella.getStoreMeta().isDeleted())
                    .isEqualTo(false);
            softly.assertThat(modifiedUmbrella.isRentable())
                    .isEqualTo(true);
            softly.assertThat(modifiedUmbrella.isDeleted())
                    .isEqualTo(false);
            softly.assertAll();

            assertAll(
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByUuid(50L),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsById(1L),
                    () -> then(storeMetaService).should(times(1))
                            .findById(5L),
                    () -> then(umbrellaRepository).should(times(1))
                            .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("수정하려는 우산 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        void withNonExistingId() {

            // given
            given(storeMetaService.findById(5L))
                    .willReturn(foundStoreMeta);
            given(umbrellaRepository.existsById(1L))
                    .willReturn(false);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() ->
                            umbrellaService.modifyUmbrella(1L, umbrellaRequest))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(umbrellaRepository).should(never())
                            .existsByUuid(50L),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsById(1L),
                    () -> then(storeMetaService).should(times(1))
                            .findById(5L),
                    () -> then(umbrellaRepository).should(never())
                            .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("수정하려는 우산 관리번호가 이미 존재하는 경우 예외를 발생시킨다.")
        void withAlreadyExistingUuid() {

            // given
            given(storeMetaService.findById(5L))
                    .willReturn(foundStoreMeta);
            given(umbrellaRepository.existsById(1L))
                    .willReturn(true);
            given(umbrellaRepository.existsByUuid(50L))
                    .willReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() ->
                            umbrellaService.modifyUmbrella(1L, umbrellaRequest))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByUuid(50L),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsById(1L),
                    () -> then(storeMetaService).should(times(1))
                            .findById(5L),
                    () -> then(umbrellaRepository).should(never())
                            .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("추가하려고 하는 가게 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        void atNonExistingStore() {

            // given
            given(storeMetaService.findById(5L))
                    .willThrow(new IllegalArgumentException());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() ->
                            umbrellaService.modifyUmbrella(1L, umbrellaRequest))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(storeMetaService).should(times(1))
                            .findById(5L),
                    () -> then(umbrellaRepository).shouldHaveNoInteractions()
            );
        }
    }

    @Test
    void deleteUmbrella() {
    }
}