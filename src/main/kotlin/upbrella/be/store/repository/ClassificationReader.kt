package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.ClassificationType

@Component
class ClassificationReader(
    private val classificationRepository: ClassificationRepository,
) {
    fun findById(id: Long): Classification? {
        return classificationRepository.findById(id).orElse(null)
    }

    fun findByType(type: ClassificationType): List<Classification> {
        return classificationRepository.findByType(type)
    }

    fun existsById(id: Long): Boolean {
        return classificationRepository.existsById(id)
    }
}