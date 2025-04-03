package upbrella.be.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.user.entity.User
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {

    fun findBySocialId(socialId: Long): Optional<User>
    fun existsBySocialId(socialId: Long): Boolean
}