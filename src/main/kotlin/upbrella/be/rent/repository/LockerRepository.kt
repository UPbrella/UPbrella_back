package upbrella.be.rent.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.rent.entity.Locker
import java.util.*

interface LockerRepository : JpaRepository<Locker, Long> {

    fun findByStoreMetaId(storeMetaId: Long): Optional<Locker>
}