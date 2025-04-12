package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.ClassificationType
import upbrella.be.store.exception.NonExistingClassificationException

@Component
class ClassificationReader(
    private val classificationRepository: ClassificationRepository,
) {
    fun findByIdAndType(id: Long, type: ClassificationType): Classification {
        return classificationRepository.findByIdAndType(id, type)
            ?: throw NonExistingClassificationException("[ERROR] 존재하지 않는 분류입니다.")
    }

    fun findByType(type: ClassificationType): List<Classification> {
        return classificationRepository.findByType(type)
    }

    fun existsById(id: Long): Boolean {
        return classificationRepository.existsById(id)
    }
}