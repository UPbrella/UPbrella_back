package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.entity.StoreMeta

@Component
class StoreMetaWriter(
    val storeMetaRepository: StoreMetaRepository
) {
    fun save(storeMeta: StoreMeta): StoreMeta {
        return storeMetaRepository.save(storeMeta)
    }
}
