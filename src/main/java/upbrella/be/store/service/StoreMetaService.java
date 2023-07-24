package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

@Service
@RequiredArgsConstructor
public class StoreMetaService {
    private final StoreMetaRepository storeMetaRepository;
    private final UmbrellaRepository umbrellaRepository;
    public StoreMeta findById(long id) {
        return storeMetaRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));
    }

    public CurrentUmbrellaStoreResponse findCurrentStoreIdByUmbrella(long umbrellaId) {
        Umbrella foundUmbrella = umbrellaRepository.findByIdAndDeletedIsFalseAndStoreMeta_DeletedIsFalse(umbrellaId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않거나 협업지점 정보가 없는 우산입니다."));

        return CurrentUmbrellaStoreResponse.fromUmbrella(foundUmbrella);
    }
}
