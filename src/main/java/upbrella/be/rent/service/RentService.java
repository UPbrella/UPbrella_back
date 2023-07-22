package upbrella.be.rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.service.UmbrellaService;

@Service
@RequiredArgsConstructor
public class RentService {

    private final UmbrellaService umbrellaService;

    @Transactional
    public void addRental(RentUmbrellaByUserRequest rentUmbrellaByUserRequest) {

        Umbrella willRentUmbrella = umbrellaService.findById(rentUmbrellaByUserRequest.getUmbrellaId());


    }
}
