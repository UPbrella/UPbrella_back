package upbrella.be.umbrella.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.umbrella.entity.Umbrella
import java.util.Optional

interface UmbrellaRepository : JpaRepository<Umbrella, Long>, UmbrellaRepositoryCustom {

    fun findByIdAndDeletedIsFalse(id: Long): Optional<Umbrella>
    fun existsByUuidAndDeletedIsFalse(uuid: Long): Boolean
}