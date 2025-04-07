package upbrella.be.user.repository

import org.springframework.stereotype.Component
import upbrella.be.user.entity.User
import upbrella.be.user.exception.NonExistingMemberException

@Component
class UserReader(
    private val userRepository: UserRepository
) {
    fun findUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다.") }
    }

    fun findBySocialId(socialId: Long): User {
        return userRepository.findBySocialId(socialId)
            .orElseThrow { NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다. 회원 가입을 해주세요.") }
    }

    fun existsBySocialId(socialIdHash: Long): Boolean {
        return userRepository.existsBySocialId(socialIdHash)
    }

    fun findAll(): List<User> {
        return userRepository.findAll()
    }
}