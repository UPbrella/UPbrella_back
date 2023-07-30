package upbrella.be.rent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.service.UmbrellaService;
import upbrella.be.user.entity.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RentServiceTest {

    @Mock
    private UmbrellaService umbrellaService;
    @Mock
    private StoreMetaService storeMetaService;
    @Mock
    private RentRepository rentRepository;
    @InjectMocks
    private RentService rentService;

    @Nested
    @DisplayName("지역 분류, 협력 지점 고유번호, 우산 고유번호, 선택적으로 상태 신고 내역을 입력받아")
    class addRentTest {

        private RentUmbrellaByUserRequest rentUmbrellaByUserRequest;
        private StoreMeta foundStoreMeta;
        private Umbrella foundUmbrella;
        private User userToRent;
        private History history;

        @BeforeEach
        void setUp() {
            rentUmbrellaByUserRequest = RentUmbrellaByUserRequest.builder()
                    .region("신촌")
                    .storeId(25L)
                    .umbrellaId(99L)
                    .conditionReport("상태 양호")
                    .build();

            foundStoreMeta = StoreMeta.builder()
                    .id(25L)
                    .name("motive study cafe")
                    .thumbnail("thumb")
                    .deleted(false)
                    .build();

            foundUmbrella = Umbrella.builder()
                    .id(99L)
                    .uuid(99L)
                    .deleted(false)
                    .storeMeta(foundStoreMeta)
                    .rentable(true)
                    .build();

            userToRent = User.builder()
                    .id(11L)
                    .name("테스터")
                    .phoneNumber("010-1234-5678")
                    .adminStatus(false)
                    .build();

            history = History.builder()
                    .id(33L)
                    .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                    .umbrella(foundUmbrella)
                    .user(userToRent)
                    .rentStoreMeta(foundStoreMeta)
                    .build();
        }

        @Test
        @DisplayName("대여 이력에 정상적으로 추가하 수 있다.")
        void success() {

            // given
            given(storeMetaService.findStoreMetaById(25L))
                    .willReturn(foundStoreMeta);
            given(umbrellaService.findUmbrellaById(99L))
                    .willReturn(foundUmbrella);
            given(rentRepository.save(any(History.class)))
                    .willReturn(history);

            // when
            History addedRental = rentService.addRental(rentUmbrellaByUserRequest, userToRent);

            // when & then
            assertAll(() -> assertThat(addedRental)
                            .usingRecursiveComparison()
                            .isEqualTo(history),
                    () -> then(umbrellaService).should(times(1))
                            .findUmbrellaById(99L),
                    () -> then(storeMetaService).should(times(1))
                            .findStoreMetaById(25L),
                    () -> then(rentRepository).should(times(1))
                            .save(any(History.class))
            );
        }

        @Test
        @DisplayName("해당 협업 지점의 고유 번호가 존재하지 않으면 예외를 발생시킨다.")
        void isNotExistingStore() {

            // given
            given(umbrellaService.findUmbrellaById(99L))
                    .willReturn(foundUmbrella);
            given(storeMetaService.findStoreMetaById(25L))
                    .willThrow(new IllegalArgumentException());

            // when & then
            assertAll(() -> assertThatThrownBy(() ->
                            rentService.addRental(rentUmbrellaByUserRequest, userToRent))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(umbrellaService).should(times(1))
                            .findUmbrellaById(99L),
                    () -> then(storeMetaService).should(times(1))
                            .findStoreMetaById(25L),
                    () -> then(rentRepository).shouldHaveNoInteractions()
            );
        }

        @Test
        @DisplayName("해당 우산의 고유번호가 존재하지 않으면 예외를 발생시킨다.")
        void isNotExistingUmbrella() {

            // given
            given(umbrellaService.findUmbrellaById(99L))
                    .willThrow(new IllegalArgumentException());

            // when & then
            assertAll(() -> assertThatThrownBy(() ->
                            rentService.addRental(rentUmbrellaByUserRequest, userToRent))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> then(umbrellaService).should(times(1))
                            .findUmbrellaById(99L),
                    () -> then(storeMetaService).shouldHaveNoInteractions(),
                    () -> then(rentRepository).shouldHaveNoInteractions()
            );
        }
    }
}