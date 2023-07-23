package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.storeRepository.StoreMetaRepository;
import upbrella.be.store.entity.StoreMeta;

@Service
@RequiredArgsConstructor
public class StoreMetaService {

    private final StoreMetaRepository storeMetaRepository;

    public StoreMeta findById(long id) {
        return storeMetaRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));
    }
}
