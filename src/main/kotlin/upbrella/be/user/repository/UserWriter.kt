package upbrella.be.user.repository

import org.springframework.stereotype.Component
import upbrella.be.user.entity.User

@Component
class UserWriter(
    private val userRepository: UserRepository,
) {
    fun save(user: User): User {
        return userRepository.save(user)
    }
}