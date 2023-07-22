package upbrella.be.umbrella.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UmbrellaService {
    private final UmbrellaRepository umbrellaRepository;
    private final StoreMetaService storeMetaService;

    public List<UmbrellaResponse> findAllUmbrellas() {
        return null;
    }

    public List<UmbrellaResponse> findUmbrellasByStoreId(long storeId) {
        return null;
    }

    @Transactional
    public Umbrella addUmbrella(UmbrellaRequest umbrellaRequest) {

        StoreMeta storeMeta = storeMetaService.findById(umbrellaRequest.getStoreMetaId());

        if (umbrellaRepository.existsByUuidAndDeletedIsFalse(umbrellaRequest.getUuid())) {
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

    @Transactional
    public Umbrella modifyUmbrella(long id, UmbrellaRequest umbrellaRequest) {

        StoreMeta storeMeta = storeMetaService.findById(umbrellaRequest.getStoreMetaId());
        if (!umbrellaRepository.existsByIdAndDeletedIsFalse(id)) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 우산 고유번호입니다.");
        }
        if (umbrellaRepository.existsByUuidAndDeletedIsFalse(umbrellaRequest.getUuid())) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 우산 관리 번호입니다.");
        }

        return umbrellaRepository.save(
                Umbrella.ofUpdated(
                        id,
                        storeMeta,
                        umbrellaRequest.getUuid(),
                        umbrellaRequest.isRentable()
                )
        );
    }

    @Transactional
    public void deleteUmbrella(long id) {

        // 이미 삭제된 경우도 포함
        Umbrella foundUmbrella = umbrellaRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 우산 고유번호입니다."));
        foundUmbrella.delete();
    }

}
