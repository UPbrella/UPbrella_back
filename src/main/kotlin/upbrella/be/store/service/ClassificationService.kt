package upbrella.be.store.service

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.store.dto.request.CreateClassificationRequest
import upbrella.be.store.dto.request.CreateSubClassificationRequest
import upbrella.be.store.dto.response.AllClassificationResponse
import upbrella.be.store.dto.response.AllSubClassificationResponse
import upbrella.be.store.dto.response.SingleClassificationResponse
import upbrella.be.store.dto.response.SingleSubClassificationResponse
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.ClassificationType
import upbrella.be.store.exception.AssignedClassificationException
import upbrella.be.store.exception.IncorrectClassificationException
import upbrella.be.store.exception.NonExistingClassificationException
import upbrella.be.store.repository.ClassificationRepository

@Service
class ClassificationService(
    private val classificationRepository: ClassificationRepository,
    @Lazy private val storeMetaService: StoreMetaService
) {

    fun createClassification(request: CreateClassificationRequest): Classification {
        return classificationRepository.save(Classification.ofCreateClassification(request))
    }

    fun createSubClassification(request: CreateSubClassificationRequest): Classification {
        return classificationRepository.save(Classification.ofCreateSubClassification(request))
    }

    @Transactional
    fun deleteClassification(id: Long) {
        if (!classificationRepository.existsById(id)) {
            throw NonExistingClassificationException("[ERROR] 존재하지 않는 대분류입니다.")
        }

        if (storeMetaService.existByClassificationId(id)) {
            throw AssignedClassificationException("[ERROR] 해당 대분류에 속한 협업지점이 존재합니다.")
        }

        classificationRepository.deleteById(id)
    }

    fun deleteSubClassification(id: Long) {
        if (!classificationRepository.existsById(id)) {
            throw NonExistingClassificationException("[ERROR] 존재하지 않는 소분류입니다.")
        }

        if (storeMetaService.existByClassificationId(id)) {
            throw AssignedClassificationException("[ERROR] 해당 소분류에 속한 협업지점이 존재합니다.")
        }

        classificationRepository.deleteById(id)
    }

    fun findAllClassification(): AllClassificationResponse {
        val allByClassification = classificationRepository.findByType(ClassificationType.CLASSIFICATION)
        val classifications = mutableListOf<SingleClassificationResponse>()

        for (classification in allByClassification) {
            classifications.add(SingleClassificationResponse.ofCreateClassification(classification))
        }

        return AllClassificationResponse(
            classifications = classifications
        )
    }

    fun findAllSubClassification(): AllSubClassificationResponse {
        val allByClassification = classificationRepository.findByType(ClassificationType.SUB_CLASSIFICATION)
        val classifications = mutableListOf<SingleSubClassificationResponse>()

        for (classification in allByClassification) {
            classifications.add(SingleSubClassificationResponse.ofCreateSubClassification(classification))
        }

        return AllSubClassificationResponse(
            subClassifications = classifications
        )
    }

    fun findClassificationById(id: Long): Classification {
        val classification = findClassificationEntityById(id)
        if (classification.type != ClassificationType.CLASSIFICATION) {
            throw IncorrectClassificationException("[ERROR] Classification이 아닙니다.")
        }
        return classification
    }

    fun findSubClassificationById(id: Long): Classification {
        val classification = findClassificationEntityById(id)
        if (classification.type != ClassificationType.SUB_CLASSIFICATION) {
            throw IncorrectClassificationException("[ERROR] SubClassification이 아닙니다.")
        }
        return classification
    }

    private fun findClassificationEntityById(id: Long): Classification {
        return classificationRepository.findById(id)
            .orElseThrow { NonExistingClassificationException("[ERROR] 존재하지 않는 분류입니다.") }
    }
}