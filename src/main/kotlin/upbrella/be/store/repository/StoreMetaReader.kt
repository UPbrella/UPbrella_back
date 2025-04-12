package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.exception.NonExistingStoreMetaException

@Component
class StoreMetaReader(
    val storeMetaRepository: StoreMetaRepository
) {
    fun findByClassificationIdAndDeletedIsFalse(id: Long): StoreMeta? {
        return storeMetaRepository.findByClassificationIdAndDeletedIsFalse(id).orElse(null)
    }

    fun existByClassificationId(classificationId: Long): Boolean {
        return storeMetaRepository.existsByClassificationIdAndDeletedIsFalse(classificationId)
    }

    fun existsById(id: Long): Boolean {
        return storeMetaRepository.existsById(id)
    }

    fun findById(id: Long): StoreMeta {
        return storeMetaRepository.findById(id)
            .orElseThrow { NonExistingStoreMetaException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다.") }
    }

    fun findAllStoresByClassification(classificationId: Long): List<StoreMetaWithUmbrellaCount> {
        return storeMetaRepository.findAllStoresByClassification(classificationId)
    }

}
