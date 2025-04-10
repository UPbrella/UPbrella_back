package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.ClassificationType

@Component
class ClassificationWriter(
    private val classificationRepository: ClassificationRepository,
) {
    fun save (classification: Classification): Classification {
        return classificationRepository.save(classification)
    }

    fun deleteById(id: Long) {
        classificationRepository.deleteById(id)
    }

    fun saveAll(classifications: List<Classification>) {
        classificationRepository.saveAll(classifications)
    }
}