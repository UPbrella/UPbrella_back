package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.StoreRepository.StoreMetaRepository;
import upbrella.be.store.entity.StoreMeta;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreMetaService {
    private final StoreMetaRepository storeMetaRepository;

    public Optional<StoreMeta> findByStoreMetaId(long id) {
        return storeMetaRepository.findById(id);
    }
}
