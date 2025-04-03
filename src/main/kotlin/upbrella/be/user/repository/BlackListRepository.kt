package upbrella.be.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.user.entity.BlackList

interface BlackListRepository : JpaRepository<BlackList, Long> {

    fun existsBySocialId(socialId: Long): Boolean
}