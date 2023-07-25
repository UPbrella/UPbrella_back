package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.dto.request.CoordinateRequest;
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse;
import upbrella.be.store.entity.StoreMeta;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreMetaService {
    private final StoreMetaRepository storeMetaRepository;

    public StoreMeta findById(long id) {

        return storeMetaRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));
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
