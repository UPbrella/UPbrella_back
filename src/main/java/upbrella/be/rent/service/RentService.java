package upbrella.be.rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;
import upbrella.be.umbrella.service.UmbrellaService;
import upbrella.be.user.entity.User;
import upbrella.be.user.service.UserService;

@Service
@RequiredArgsConstructor
public class RentService {

    private final UmbrellaRepository umbrellaRepository;
    private final UserService userService;
    private final StoreMetaService storeMetaService;
    private final RentRepository rentRepository;

    @Transactional
    public void addRental(RentUmbrellaByUserRequest rentUmbrellaByUserRequest, User userToRent) {

        Umbrella willRentUmbrella = umbrellaRepository.findByUuidAndDeletedIsFalse(rentUmbrellaByUserRequest.getUuid())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당 우산이 존재하지 않습니다."));

        StoreMeta rentalStore = storeMetaService.findStoreMetaById(rentUmbrellaByUserRequest.getStoreId());

        rentRepository.save(
                History.ofCreatedByNewRent(
                        willRentUmbrella,
                        userToRent,
                        rentalStore)
        );
    }
}
