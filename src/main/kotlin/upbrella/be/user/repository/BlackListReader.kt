package upbrella.be.user.repository

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import upbrella.be.user.entity.BlackList

@Component
class BlackListReader(
    private val blackListRepository: BlackListRepository,
) {
    fun findById(blackListId: Long): BlackList? {
        return blackListRepository.findByIdOrNull(blackListId)
    }
    fun existsBySocialId(socialIdHash: Long): Boolean {
        return blackListRepository.existsBySocialId(socialIdHash)
    }

    fun findAll(): List<BlackList> {
        return blackListRepository.findAll()
    }
}
