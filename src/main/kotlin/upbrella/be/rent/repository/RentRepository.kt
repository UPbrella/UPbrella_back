package upbrella.be.rent.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.rent.entity.History
import java.util.Optional

interface RentRepository : JpaRepository<History, Long>, RentRepositoryCustom {
    fun findByUserIdAndReturnedAtIsNull(userId: Long): Optional<History>

    fun countByRentStoreMetaId(storeId: Long): Long

    fun countAllByReturnedAtIsNotNullAndPaidAtIsNotNullAndRefundedAtIsNull(): Long
}