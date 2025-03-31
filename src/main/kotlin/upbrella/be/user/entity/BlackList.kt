package upbrella.be.user.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class BlackList(
    val socialId: Long,
    val blockedAt: LocalDateTime,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    companion object {
        @JvmStatic
        fun createNewBlackList(socialId: Long, blockedAt: LocalDateTime = LocalDateTime.now()): BlackList {
            return BlackList(
                socialId = socialId,
                blockedAt = blockedAt,
            )
        }
    }
}