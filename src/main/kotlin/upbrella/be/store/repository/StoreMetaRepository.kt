package upbrella.be.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.store.entity.StoreMeta

interface StoreMetaRepository : JpaRepository<StoreMeta, Long>, StoreMetaRepositoryCustom {

    fun existsByClassificationIdAndDeletedIsFalse(classificationId: Long): Boolean
}
