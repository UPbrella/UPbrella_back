package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.response.StoreFindByIdResponse;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.umbrella.service.UmbrellaService;

@RequiredArgsConstructor
@Service
public class StoreDetailService {

    private final UmbrellaService umbrellaService;
    private final StoreDetailRepository storeDetailRepository;

    @Transactional
    public StoreFindByIdResponse findStoreDetailByStoreMetaId(long storeMetaId) {

        StoreDetail storeDetail = storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeMetaId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당하는 협업 지점이 존재하지 않습니다."));

        int availableUmbrellaCount = umbrellaService.countAvailableUmbrellaAtStore(storeMetaId);

        return StoreFindByIdResponse.fromStoreDetail(storeDetail, availableUmbrellaCount);
    }
}
