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
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;
import upbrella.be.umbrella.service.UmbrellaService;
import upbrella.be.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RentServiceTest {

    @Mock
    private UmbrellaRepository umbrellaRepository;
    @Mock
    private StoreMetaService storeMetaService;
    @Mock
    private RentRepository rentRepository;
    @InjectMocks
    private RentService rentService;

    @Nested
    @DisplayName("지역 분류, 협력 지점 고유번호, 우산 고유번호, 선택적으로 상태 신고 내욕을 입력받아")
    class addRentTest {

        private RentUmbrellaByUserRequest rentUmbrellaByUserRequest;
        private StoreMeta foundStoreMeta;
        private Umbrella foundUmbrella;

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
                    .uuid(99L)
                    .deleted(false)
                    .storeMeta(foundStoreMeta)
                    .rentable(true)
                    .build();
        }

        @Test
        @DisplayName("대여 이력에 정상적으로 추가하 수 있다.")
        void success() {

            given(storeMetaService.findById(25L))
                    .willReturn(foundStoreMeta);
            given(umbrellaRepository.existsByUuidAndDeletedIsFalse(99L))
                    .willReturn(false);
            given(umbrellaRepository.save(any(Umbrella.class)))
                    .willReturn(foundUmbrella);
        }

    }

}