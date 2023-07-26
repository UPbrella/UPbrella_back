package upbrella.be.rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;
import upbrella.be.user.entity.User;

@Service
@RequiredArgsConstructor
public class RentService {

    private final UmbrellaRepository umbrellaRepository;
    private final StoreMetaRepository storeMetaRepository;
    private final RentRepository rentRepository;

    @Transactional
    public void addRental(RentUmbrellaByUserRequest rentUmbrellaByUserRequest, User userToRent) {

        // 사용자가 이미 대여 중인 우산이 있으면 대여하지 못하도록 예외 처리

        Umbrella willRentUmbrella = umbrellaRepository.findByUuidAndDeletedIsFalse(rentUmbrellaByUserRequest.getUuid())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당 우산이 존재하지 않습니다."));

        StoreMeta rentalStore = storeMetaRepository.findByIdAndDeletedIsFalse(rentUmbrellaByUserRequest.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));

        rentRepository.save(
                History.ofCreatedByNewRent(
                        willRentUmbrella,
                        userToRent,
                        rentalStore)
        );
    }
}
