package upbrella.be.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.user.entity.User
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    @org.springframework.data.jpa.repository.Lock(javax.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from User u where u.id = :id")
    fun findLockedById(@org.springframework.data.repository.query.Param("id") id: Long): Optional<User>

    fun findBySocialId(socialId: Long): Optional<User>
    fun existsBySocialId(socialId: Long): Boolean
}
