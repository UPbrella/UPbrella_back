package upbrella.be.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.user.dto.response.AllBlackListResponse
import upbrella.be.user.exception.BlackListUserException
import upbrella.be.user.repository.BlackListReader
import upbrella.be.user.repository.BlackListWriter
import upbrella.be.user.repository.UserReader

@Service
class BlackListService(
    private val blackListReader: BlackListReader,
    private val blackListWriter: BlackListWriter,
) {
    @Transactional
    fun findBlackList(): AllBlackListResponse {
        val blackLists = blackListReader.findAll()
        return AllBlackListResponse.of(blackLists)
    }

    @Transactional
    fun deleteBlackList(blackListId: Long) {
        blackListWriter.deleteById(blackListId)
    }

    @Transactional(readOnly = true)
    fun checkBlackList(userId: Long) {
        blackListReader.findById(userId)?.let { throw BlackListUserException("[ERROR] 정지된 회원입니다. 정지된 회원은 이용이 불가능합니다.") }
    }
}