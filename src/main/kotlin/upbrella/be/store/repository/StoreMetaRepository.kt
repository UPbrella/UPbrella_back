package upbrella.be.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.store.entity.StoreMeta
import java.util.Optional

interface StoreMetaRepository : JpaRepository<StoreMeta, Long>, StoreMetaRepositoryCustom {
    fun findByClassificationIdAndDeletedIsFalse(id: Long): Optional<StoreMeta>
}