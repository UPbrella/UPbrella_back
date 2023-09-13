package upbrella.be.umbrella.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import upbrella.be.rent.service.RentService;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.exception.NonExistingStoreMetaException;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest;
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.dto.response.UmbrellaStatisticsResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.exception.ExistingUmbrellaUuidException;
import upbrella.be.umbrella.exception.NonExistingUmbrellaException;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UmbrellaService {
    private final UmbrellaRepository umbrellaRepository;
    private final StoreMetaService storeMetaService;
    private final RentService rentService;

    public UmbrellaService(UmbrellaRepository umbrellaRepository, StoreMetaService storeMetaService, @Lazy RentService rentService) {
        this.umbrellaRepository = umbrellaRepository;
        this.storeMetaService = storeMetaService;
        this.rentService = rentService;
    }

    public List<UmbrellaResponse> findAllUmbrellas(Pageable pageable) {

        return umbrellaRepository.findByDeletedIsFalseOrderById(pageable)
                .stream().map(UmbrellaResponse::fromUmbrella)
                .collect(Collectors.toList());
    }

    public List<UmbrellaResponse> findUmbrellasByStoreId(long storeId, Pageable pageable) {

        return umbrellaRepository.findByStoreMetaIdAndDeletedIsFalseOrderById(storeId, pageable)
                .stream().map(UmbrellaResponse::fromUmbrella)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addUmbrella(UmbrellaCreateRequest umbrellaCreateRequest) {

        StoreMeta storeMeta = storeMetaService.findStoreMetaById(umbrellaCreateRequest.getStoreMetaId());

        if (umbrellaRepository.existsByUuidAndDeletedIsFalse(umbrellaCreateRequest.getUuid())) {
            throw new ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리 번호입니다.");
        }

        umbrellaRepository.save(Umbrella.ofCreated(umbrellaCreateRequest, storeMeta));
    }

    @Transactional
    public void modifyUmbrella(long id, UmbrellaModifyRequest umbrellaModifyRequest) {

        StoreMeta storeMeta = storeMetaService.findStoreMetaById(umbrellaModifyRequest.getStoreMetaId());

        Umbrella foundUmbrella = umbrellaRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 고유번호입니다."));


        if (foundUmbrella.getUuid() != umbrellaModifyRequest.getUuid()) {
            if (umbrellaRepository.existsByUuidAndDeletedIsFalse(umbrellaModifyRequest.getUuid())) {
                throw new ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리 번호입니다.");
            }
        }

        foundUmbrella.update(umbrellaModifyRequest, storeMeta);
    }

    @Transactional
    public void deleteUmbrella(long id) {

        Umbrella foundUmbrella = findUmbrellaById(id);
        foundUmbrella.delete();
    }

    public Umbrella findUmbrellaById(long id) {

        return umbrellaRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 고유번호입니다."));
    }

    public long countAvailableUmbrellaAtStore(long storeMetaId) {

        return umbrellaRepository.countRentableUmbrellasByStore(storeMetaId);
    }

    public UmbrellaStatisticsResponse getUmbrellaAllStatistics() {

        long totalUmbrella = umbrellaRepository.countAllUmbrellas();
        long availableUmbrella = umbrellaRepository.countRentableUmbrellas();
        long rentedUmbrella = umbrellaRepository.countRentedUmbrellas();
        long missingUmbrella = umbrellaRepository.countMissingUmbrellas();
        long totalRent = rentService.countTotalRent();

        return UmbrellaStatisticsResponse.fromCounts(totalUmbrella, availableUmbrella,
                rentedUmbrella, missingUmbrella, totalRent);
    }

    public UmbrellaStatisticsResponse getUmbrellaStatisticsByStoreId(long storeId) {

        if (!storeMetaService.existByStoreId(storeId)) {
            throw new NonExistingStoreMetaException("[ERROR] 존재하지 않는 매장 고유번호입니다.");
        }

        long totalUmbrellaByStoreId = umbrellaRepository.countAllUmbrellasByStore(storeId);
        long availableUmbrellaByStoreId = umbrellaRepository.countRentableUmbrellasByStore(storeId);
        long rentedUmbrellaByStoreId = umbrellaRepository.countRentedUmbrellasByStore(storeId);
        long missingUmbrellaByStoreId = umbrellaRepository.countMissingUmbrellasByStore(storeId);
        long totalRentByStoreId = rentService.countTotalRentByStoreId(storeId);

        return UmbrellaStatisticsResponse.fromCounts(totalUmbrellaByStoreId, availableUmbrellaByStoreId,
                rentedUmbrellaByStoreId, missingUmbrellaByStoreId, totalRentByStoreId);
    }
}
