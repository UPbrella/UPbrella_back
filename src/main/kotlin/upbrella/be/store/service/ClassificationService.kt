package upbrella.be.store.service

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
import upbrella.be.store.exception.NonExistingClassificationException
import upbrella.be.store.repository.ClassificationReader
import upbrella.be.store.repository.ClassificationWriter
import upbrella.be.store.repository.StoreMetaReader

@Service
class ClassificationService(
    private val classificationReader: ClassificationReader,
    private val classificationWriter: ClassificationWriter,
    private val storeMetaReader: StoreMetaReader
) {

    fun createClassification(request: CreateClassificationRequest): Classification {
        return classificationWriter.save(Classification.ofCreateClassification(request))
    }

    fun createSubClassification(request: CreateSubClassificationRequest): Classification {
        return classificationWriter.save(Classification.ofCreateSubClassification(request))
    }

    @Transactional
    fun deleteClassification(id: Long) {
        if (!classificationReader.existsById(id)) {
            throw NonExistingClassificationException("[ERROR] 존재하지 않는 대분류입니다.")
        }

        if (storeMetaReader.existByClassificationId(id)) {
            throw AssignedClassificationException("[ERROR] 해당 대분류에 속한 협업지점이 존재합니다.")
        }

        classificationWriter.deleteById(id)
    }

    fun deleteSubClassification(id: Long) {
        if (!classificationReader.existsById(id)) {
            throw NonExistingClassificationException("[ERROR] 존재하지 않는 소분류입니다.")
        }

        if (storeMetaReader.existByClassificationId(id)) {
            throw AssignedClassificationException("[ERROR] 해당 소분류에 속한 협업지점이 존재합니다.")
        }

        classificationWriter.deleteById(id)
    }

    fun findAllClassification(): AllClassificationResponse {
        val allByClassification = classificationReader.findByType(ClassificationType.CLASSIFICATION)
        val classifications = mutableListOf<SingleClassificationResponse>()

        for (classification in allByClassification) {
            classifications.add(SingleClassificationResponse.ofCreateClassification(classification))
        }

        return AllClassificationResponse(
            classifications = classifications
        )
    }

    fun findAllSubClassification(): AllSubClassificationResponse {
        val allByClassification = classificationReader.findByType(ClassificationType.SUB_CLASSIFICATION)
        val classifications = mutableListOf<SingleSubClassificationResponse>()

        for (classification in allByClassification) {
            classifications.add(SingleSubClassificationResponse.ofCreateSubClassification(classification))
        }

        return AllSubClassificationResponse(
            subClassifications = classifications
        )
    }
}
