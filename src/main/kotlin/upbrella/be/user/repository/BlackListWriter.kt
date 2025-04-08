package upbrella.be.user.repository

import org.springframework.stereotype.Component
import upbrella.be.user.entity.BlackList

@Component
class BlackListWriter(
    private val blackListRepository: BlackListRepository,
) {
    fun save(blackList: BlackList) {
        blackListRepository.save(blackList)
    }

    fun deleteById(blackListId: Long) {
        blackListRepository.deleteById(blackListId)
    }
}