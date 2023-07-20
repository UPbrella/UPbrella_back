package upbrella.be.umbrella.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UmbrellaService {
    private final UmbrellaRepository umbrellaRepository;
    private final StoreMetaRepository storeMetaRepository;
    public List<UmbrellaResponse> findAllUmbrellas() {
        return null;
    }
    public List<UmbrellaResponse> findUmbrellasByStoreId(long storeId) {
        return null;
    }

    public Umbrella addUmbrella(UmbrellaRequest umbrellaRequest) {

        StoreMeta storeMeta = storeMetaRepository.findById(umbrellaRequest.getStoreMetaId())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));

        if (umbrellaRepository.existsByUuid(umbrellaRequest.getUuid())) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 우산 관리 번호입니다.");
        }

        return umbrellaRepository.save(
                Umbrella.ofCreated(
                        storeMeta,
                        umbrellaRequest.getUuid(),
                        umbrellaRequest.isRentable()
                )
        );
    }
    public void modifyUmbrella(long id, UmbrellaRequest umbrellaRequest) {

    }
    public void deleteUmbrella(long id) {}

}
