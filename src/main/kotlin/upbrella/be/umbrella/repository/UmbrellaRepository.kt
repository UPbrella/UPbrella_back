package upbrella.be.umbrella.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param
import upbrella.be.umbrella.entity.Umbrella
import java.util.Optional
import javax.persistence.LockModeType
import javax.persistence.QueryHint

interface UmbrellaRepository : JpaRepository<Umbrella, Long>, UmbrellaRepositoryCustom {

    fun findByIdAndDeletedIsFalse(id: Long): Optional<Umbrella>
    fun existsByUuidAndDeletedIsFalse(uuid: Long): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "javax.persistence.lock.timeout", value = "3000"))
    @Query("SELECT u FROM Umbrella u WHERE u.id = :id AND u.deleted = false")
    fun findByIdAndDeletedIsFalseForUpdate(@Param("id") id: Long): Optional<Umbrella>
}