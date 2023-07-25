package upbrella.be.umbrella.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UmbrellaServiceTest {
    @Mock
    private UmbrellaRepository umbrellaRepository;
    @Mock
    private StoreMetaRepository storeMetaRepository;
    @InjectMocks
    private UmbrellaService umbrellaService;

    @Nested
    @DisplayName("페이지 번호와 페이지 크기를 입력받아")
    class findAllUmbrellasTest {
        private List<Umbrella> umbrellas = new ArrayList<>();
        private StoreMeta storeMeta;
        private UmbrellaResponse umbrellaResponse;

        @BeforeEach
        void setUp() {
            storeMeta = StoreMeta.builder()
                    .id(2L)
                    .name("name")
                    .thumbnail("thumb")
                    .deleted(false)
                    .build();

            umbrellas.add(Umbrella.builder()
                    .id(1L)
                    .uuid(43L)
                    .deleted(false)
                    .storeMeta(storeMeta)
                    .rentable(true)
                    .build());

            umbrellaResponse = UmbrellaResponse.builder()
                    .id(1L)
                    .uuid(43L)
                    .storeMetaId(2L)
                    .rentable(true)
                    .build();
        }

        @DisplayName("해당하는 페이지의 우산 고유번호, 우산 관리번호, 협력 지점 고유번호, 대여 가능 여부를 포함하는 우산 목록 정보를 반환한다.")
        @Test
        void success() {

            //given
            Pageable pageable = PageRequest.of(0, 5);
            given(umbrellaRepository.findByDeletedIsFalseOrderById(pageable))
                    .willReturn(umbrellas);

            //when
            List<UmbrellaResponse> umbrellaResponseList = umbrellaService.findAllUmbrellas(pageable);

            //then
            assertAll(
                    () -> assertThat(umbrellaResponseList.size())
                            .isEqualTo(1),
                    () -> assertThat(umbrellaResponseList.get(0))
                            .usingRecursiveComparison()
                            .isEqualTo(umbrellaResponse)
            );
        }

        @DisplayName("해당하는 우산이 없으면 빈 객체를 반환한다.")
        @Test
        void empty() {

            //given
            Pageable pageable = PageRequest.of(0, 5);
            given(umbrellaRepository.findByDeletedIsFalseOrderById(pageable))
                    .willReturn(List.of());

            //when
            List<UmbrellaResponse> umbrellaResponseList = umbrellaService.findAllUmbrellas(pageable);

            //then
            assertThat(umbrellaResponseList).isEmpty();
        }
    }

    @Nested
    @DisplayName("협력 지점의 고유번호와 페이지 번호, 페이지 크기를 입력받아")
    class findUmbrellasByStoreIdTest {
        private List<Umbrella> umbrellas = new ArrayList<>();
        private StoreMeta storeMeta;
        private UmbrellaResponse umbrellaResponse;
        @BeforeEach
        void setUp() {
            storeMeta = StoreMeta.builder()
                    .id(2L)
                    .name("name")
                    .thumbnail("thumb")
                    .deleted(false)
                    .build();

            umbrellas.add(Umbrella.builder()
                    .id(1L)
                    .uuid(43L)
                    .deleted(false)
                    .storeMeta(storeMeta)
                    .rentable(true)
                    .build());

            umbrellaResponse = UmbrellaResponse.builder()
                    .id(1L)
                    .uuid(43L)
                    .storeMetaId(2L)
                    .rentable(true)
                    .build();
        }

        @DisplayName("해당하는 페이지의 우산 고유번호, 우산 관리번호, 협력 지점 고유번호, 대여 가능 여부를 포함하는 우산 목록 정보를 반환한다.")
        @Test
        void success() {
            //given
            Pageable pageable = PageRequest.of(0, 5);
            given(umbrellaRepository.findByStoreMetaIdAndDeletedIsFalseOrderById(2L, pageable))
                    .willReturn(umbrellas);

            //when
            List<UmbrellaResponse> umbrellaResponseList = umbrellaService.findUmbrellasByStoreId(2L, pageable);

            //then
            assertAll(
                    () -> assertThat(umbrellaResponseList.size())
                            .isEqualTo(1),
                    () -> assertThat(umbrellaResponseList.get(0))
                            .usingRecursiveComparison()
                            .isEqualTo(umbrellaResponse)
            );
        }

        @DisplayName("해당하는 우산이 없으면 빈 객체를 반환한다.")
        @Test
        void empty() {

            //given
            Pageable pageable = PageRequest.of(0, 5);
            given(umbrellaRepository.findByStoreMetaIdAndDeletedIsFalseOrderById(2L, pageable))
                    .willReturn(List.of());

            //when
            List<UmbrellaResponse> umbrellaResponseList = umbrellaService.findUmbrellasByStoreId(2L, pageable);

            //then
            assertThat(umbrellaResponseList).isEmpty();
        }
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
            given(storeMetaRepository.findByIdAndDeletedIsFalse(2L))
                    .willReturn(Optional.ofNullable(foundStoreMeta));
            given(umbrellaRepository.existsByUuidAndDeletedIsFalse(43))
                    .willReturn(false);
            given(umbrellaRepository.save(any(Umbrella.class)))
                    .willReturn(umbrella);

            // when
            Umbrella addedUmbrella = umbrellaService.addUmbrella(umbrellaRequest);

            // then
            assertAll(
                    () -> assertThat(addedUmbrella.getUuid())
                            .isEqualTo(43),
                    () -> assertThat(addedUmbrella.getStoreMeta().getId())
                            .isEqualTo(2L),
                    () -> assertThat(addedUmbrella.getStoreMeta().getName())
                            .isEqualTo("name"),
                    () -> assertThat(addedUmbrella.getStoreMeta().getThumbnail())
                            .isEqualTo("thumb"),
                    () -> assertThat(addedUmbrella.getStoreMeta().isDeleted())
                            .isEqualTo(false),
                    () -> assertThat(addedUmbrella.isRentable())
                            .isEqualTo(true),
                    () -> assertThat(addedUmbrella.isDeleted())
                            .isEqualTo(false),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByUuidAndDeletedIsFalse(43),
                    () -> then(storeMetaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(2L),
                    () -> then(umbrellaRepository).should(times(1))
                            .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("우산 고유번호가 이미 존재하는 경우 예외를 발생시킨다.")
        void withSameId() {

            // given
            given(storeMetaRepository.findByIdAndDeletedIsFalse(2L))
                    .willReturn(Optional.ofNullable(foundStoreMeta));
            given(umbrellaRepository.existsByUuidAndDeletedIsFalse(43))
                    .willReturn(true);
            // when
            assertThatThrownBy(() -> umbrellaService.addUmbrella(umbrellaRequest))
                    .isInstanceOf(IllegalArgumentException.class);

            // then
            assertAll(
                    () -> then(storeMetaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(2L),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByUuidAndDeletedIsFalse(43),
                    () -> then(umbrellaRepository).should(never())
                            .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("추가하려고 하는 가게 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        void atNonExistingStore() {

            // given
            given(storeMetaRepository.findByIdAndDeletedIsFalse(2L))
                    .willReturn(Optional.ofNullable(null));

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> umbrellaService.addUmbrella(umbrellaRequest))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(storeMetaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(2L),
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
            given(storeMetaRepository.findByIdAndDeletedIsFalse(5L))
                    .willReturn(Optional.ofNullable(foundStoreMeta));
            given(umbrellaRepository.existsByIdAndDeletedIsFalse(1L))
                    .willReturn(true);
            given(umbrellaRepository.existsByUuidAndDeletedIsFalse(50L))
                    .willReturn(false);
            given(umbrellaRepository.save(any(Umbrella.class)))
                    .willReturn(umbrella);

            // when
            Umbrella modifiedUmbrella = umbrellaService.modifyUmbrella(1L, umbrellaRequest);

            // then
            assertAll(
                    () -> assertThat(modifiedUmbrella.getUuid())
                            .isEqualTo(50L),
                    () -> assertThat(modifiedUmbrella.getStoreMeta().getId())
                            .isEqualTo(5L),
                    () -> assertThat(modifiedUmbrella.getStoreMeta().getName())
                            .isEqualTo("연세대학교 파스쿠치"),
                    () -> assertThat(modifiedUmbrella.getStoreMeta().getThumbnail())
                            .isEqualTo("정면사진.jpg"),
                    () -> assertThat(modifiedUmbrella.getStoreMeta().isDeleted())
                            .isEqualTo(false),
                    () -> assertThat(modifiedUmbrella.isRentable())
                            .isEqualTo(true),
                    () -> assertThat(modifiedUmbrella.isDeleted())
                            .isEqualTo(false),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByUuidAndDeletedIsFalse(50L),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByIdAndDeletedIsFalse(1L),
                    () -> then(storeMetaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(5L),
                    () -> then(umbrellaRepository).should(times(1))
                            .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("수정하려는 우산 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        void withNonExistingId() {

            // given
            given(storeMetaRepository.findByIdAndDeletedIsFalse(5L))
                    .willReturn(Optional.ofNullable(foundStoreMeta));
            given(umbrellaRepository.existsByIdAndDeletedIsFalse(1L))
                    .willReturn(false);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() ->
                            umbrellaService.modifyUmbrella(1L, umbrellaRequest))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(umbrellaRepository).should(never())
                            .existsByUuidAndDeletedIsFalse(50L),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByIdAndDeletedIsFalse(1L),
                    () -> then(storeMetaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(5L),
                    () -> then(umbrellaRepository).should(never())
                            .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("수정하려는 우산 관리번호가 이미 존재하는 경우 예외를 발생시킨다.")
        void withAlreadyExistingUuid() {

            // given
            given(storeMetaRepository.findByIdAndDeletedIsFalse(5L))
                    .willReturn(Optional.ofNullable(foundStoreMeta));
            given(umbrellaRepository.existsByIdAndDeletedIsFalse(1L))
                    .willReturn(true);
            given(umbrellaRepository.existsByUuidAndDeletedIsFalse(50L))
                    .willReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() ->
                            umbrellaService.modifyUmbrella(1L, umbrellaRequest))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByUuidAndDeletedIsFalse(50L),
                    () -> then(umbrellaRepository).should(times(1))
                            .existsByIdAndDeletedIsFalse(1L),
                    () -> then(storeMetaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(5L),
                    () -> then(umbrellaRepository).should(never())
                            .save(any(Umbrella.class))
            );
        }

        @Test
        @DisplayName("추가하려고 하는 가게 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        void atNonExistingStore() {

            // given
            given(storeMetaRepository.findByIdAndDeletedIsFalse(5L))
                    .willReturn(Optional.ofNullable(null));

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() ->
                            umbrellaService.modifyUmbrella(1L, umbrellaRequest))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(storeMetaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(5L),
                    () -> then(umbrellaRepository).shouldHaveNoInteractions()
            );
        }
    }

    @Nested
    @DisplayName("우산의 고유번호를 입력받아")
    class deleteUmbrellaTest {
        private Umbrella umbrella;

        @BeforeEach
        void setUp() {

            umbrella = Umbrella.builder()
                    .id(1L)
                    .uuid(50L)
                    .deleted(false)
                    .storeMeta(null)
                    .rentable(true)
                    .build();
        }

        @Test
        @DisplayName("우산을 정상적으로 삭제한다.")
        void success() {

            // given
            given(umbrellaRepository.findByIdAndDeletedIsFalse(1L))
                    .willReturn(Optional.of(umbrella));

            // when
            umbrellaService.deleteUmbrella(1L);

            // then
            assertAll(
                    () -> assertThat(umbrella.getId())
                            .isEqualTo(1L),
                    () -> assertThat(umbrella.isDeleted())
                            .isEqualTo(true),
                    () -> then(umbrellaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(1L)
            );
        }

        @Test
        @DisplayName("우산이 이미 삭제되었거나 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        void alreadyDeletedOrNonExistingId() {

            // given
            given(umbrellaRepository.findByIdAndDeletedIsFalse(1L))
                    .willReturn(Optional.ofNullable(null));

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> umbrellaService.deleteUmbrella(1L))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(umbrellaRepository).should(times(1))
                            .findByIdAndDeletedIsFalse(1L)
            );
        }
    }
}