package upbrella.be.umbrella.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UmbrellaService {
    private final UmbrellaRepository umbrellaRepository;
    private final StoreMetaRepository storeMetaRepository;

    public List<UmbrellaResponse> findAllUmbrellas(Pageable pageable) {

        return umbrellaRepository.findByDeletedIsFalseOrderById(pageable)
                .stream().map(UmbrellaResponse::fromUmbrella)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<UmbrellaResponse> findUmbrellasByStoreId(long storeId, Pageable pageable) {

        return umbrellaRepository.findByStoreMetaIdAndDeletedIsFalseOrderById(storeId, pageable)
                .stream().map(UmbrellaResponse::fromUmbrella)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public Umbrella addUmbrella(UmbrellaRequest umbrellaRequest) {

        StoreMeta storeMeta = storeMetaRepository.findByIdAndDeletedIsFalse(umbrellaRequest.getStoreMetaId())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));

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

        StoreMeta storeMeta = storeMetaRepository.findByIdAndDeletedIsFalse(umbrellaRequest.getStoreMetaId())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));

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

    public Umbrella findById(long id) {

        return umbrellaRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 우산입니다."));
    }

}
