package upbrella.be.umbrella.repository

import org.springframework.stereotype.Component
import upbrella.be.umbrella.entity.Umbrella

@Component
class UmbrellaWriter(
    private val umbrellaRepository: UmbrellaRepository,
) {
    fun save(umbrella: Umbrella): Umbrella {
        return umbrellaRepository.save(umbrella)
    }
}