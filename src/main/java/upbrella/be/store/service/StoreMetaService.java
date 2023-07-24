package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.dto.request.CoordinateRequest;
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse;
import upbrella.be.store.entity.StoreMeta;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreMetaService {
    private final UmbrellaRepository umbrellaRepository;
    private final StoreMetaRepository storeMetaRepository;

    @Transactional
    public CurrentUmbrellaStoreResponse findCurrentStoreIdByUmbrella(long umbrellaId) {

        Umbrella foundUmbrella = umbrellaRepository.findByIdAndDeletedIsFalse(umbrellaId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 우산입니다."));

        if (foundUmbrella.getStoreMeta().isDeleted()) {
            throw new IllegalArgumentException("[ERROR] 삭제된 가게입니다.");
        }
        return CurrentUmbrellaStoreResponse.fromUmbrella(foundUmbrella);
    }

    public List<SingleCurrentLocationStoreResponse> findStoresInCurrentMap(CoordinateRequest coordinateRequest) {

        List<StoreMeta> storeMetaListInCurrentMap = storeMetaRepository.findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(
                coordinateRequest.getLatitudeFrom(), coordinateRequest.getLatitudeTo(),
                coordinateRequest.getLongitudeFrom(), coordinateRequest.getLongitudeTo()
        );

        return storeMetaListInCurrentMap.stream()
                .map(SingleCurrentLocationStoreResponse::fromStoreMeta)
                .collect(Collectors.toUnmodifiableList());
    }
}

