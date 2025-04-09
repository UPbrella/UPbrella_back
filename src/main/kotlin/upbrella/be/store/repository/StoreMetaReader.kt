package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount
import upbrella.be.store.entity.StoreMeta

@Component
class StoreMetaReader(
    val storeMetaRepository: StoreMetaRepository
) {
    fun findByClassificationIdAndDeletedIsFalse(id: Long): StoreMeta? {
        return storeMetaRepository.findByClassificationIdAndDeletedIsFalse(id).orElse(null)
    }

    fun existsByClassificationIdAndDeletedIsFalse(classificationId: Long): Boolean {
        return storeMetaRepository.existsByClassificationIdAndDeletedIsFalse(classificationId)
    }

    fun existsById(id: Long): Boolean {
        return storeMetaRepository.existsById(id)
    }

    fun findById(id: Long): StoreMeta? {
        return storeMetaRepository.findById(id).orElse(null)
    }

    fun findAllStoresByClassification(classificationId: Long): List<StoreMetaWithUmbrellaCount> {
        return storeMetaRepository.findAllStoresByClassification(classificationId)
    }
}
