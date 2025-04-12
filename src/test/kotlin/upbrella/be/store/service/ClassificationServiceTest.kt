package upbrella.be.store.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import upbrella.be.store.dto.request.CreateClassificationRequest
import upbrella.be.store.dto.request.CreateSubClassificationRequest
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.ClassificationType
import upbrella.be.store.exception.AssignedClassificationException
import upbrella.be.store.exception.IncorrectClassificationException
import upbrella.be.store.repository.ClassificationReader
import upbrella.be.store.repository.ClassificationWriter
import upbrella.be.store.repository.StoreMetaReader

@ExtendWith(MockitoExtension::class)
class ClassificationServiceTest {

    @Mock
    private lateinit var classificationReader: ClassificationReader

    @Mock
    private lateinit var classificationWriter: ClassificationWriter

    @Mock
    private lateinit var storeMetaReader: StoreMetaReader

    @InjectMocks
    private lateinit var classificationService: ClassificationService

    @Test
    @DisplayName("사용자는 대분류를 저장할 수 있다.")
    fun createClassificationTest() {
        // given
        val request = CreateClassificationRequest(
            name = "편의점",
            latitude = 37.1234,
            longitude = 127.1234
        )

        given(classificationWriter.save(org.mockito.kotlin.any<Classification>()))
            .willReturn(Classification())

        // when
        classificationService.createClassification(request)

        // then
        Mockito.verify(classificationWriter, Mockito.times(1)).save(org.mockito.kotlin.any<Classification>())
    }

    @Test
    @DisplayName("사용자는 소분류를 저장할 수 있다.")
    fun createSubClassificationTest() {
        // given
        val request = CreateSubClassificationRequest(
            name = "편의점"
        )

        given(classificationWriter.save(org.mockito.kotlin.any<Classification>()))
            .willReturn(Classification())

        // when
        classificationService.createSubClassification(request)

        // then
        Mockito.verify(classificationWriter, Mockito.times(1)).save(org.mockito.kotlin.any<Classification>())
    }

    @Test
    @DisplayName("사용자는 대분류를 삭제할 수 있다.")
    fun deleteClassificationTest() {
        // given
        val classificationId = 1L
        given(storeMetaReader.existByClassificationId(classificationId)).willReturn(false)
        given(classificationReader.existsById(classificationId)).willReturn(true)
        doNothing().`when`(classificationWriter).deleteById(classificationId)

        // when
        classificationService.deleteClassification(classificationId)

        // then
        Mockito.verify(classificationWriter, Mockito.times(1)).deleteById(classificationId)
    }

    @Test
    @DisplayName("사용자가 할당된 대분류를 삭제하려고 하면 예외를 발생시킨다.")
    fun unableToDeleteClassification() {
        // given
        val classificationId = 1L
        given(storeMetaReader.existByClassificationId(classificationId)).willReturn(true)
        given(classificationReader.existsById(classificationId)).willReturn(true)

        // when & then
        assertThatThrownBy { classificationService.deleteClassification(classificationId) }
            .isInstanceOf(AssignedClassificationException::class.java)
            .hasMessageContaining("[ERROR] 해당 대분류에 속한 협업지점이 존재합니다.")
    }

    @Test
    @DisplayName("사용자가 할당된 소분류를 삭제하려고 하면 예외를 발생시킨다.")
    fun unableToDeleteSubClassification() {
        // given
        val classificationId = 1L
        given(storeMetaReader.existByClassificationId(classificationId)).willReturn(true)
        given(classificationReader.existsById(classificationId)).willReturn(true)

        // when & then
        assertThatThrownBy { classificationService.deleteSubClassification(classificationId) }
            .isInstanceOf(AssignedClassificationException::class.java)
            .hasMessageContaining("[ERROR] 해당 소분류에 속한 협업지점이 존재합니다.")
    }

    @Test
    @DisplayName("사용자는 대분류를 조회할 수 있다.")
    fun findAllClassificationTest() {
        // given
        val mockClassificationList = listOf(
            Classification(
                id = 1L,
                type = ClassificationType.CLASSIFICATION,
                name = "classification_name",
                latitude = 1.0,
                longitude = 1.0
            )
        )
        given(classificationReader.findByType(ClassificationType.CLASSIFICATION))
            .willReturn(mockClassificationList)

        // when
        val result = classificationService.findAllClassification()

        // then
        assertAll(
            { assertEquals(1, result.classifications.size) },
            { assertEquals(1L, result.classifications[0].id) },
            { assertEquals("classification_name", result.classifications[0].name) }
        )
    }

    @Test
    @DisplayName("사용자는 소분류를 조회할 수 있다.")
    fun findAllSubClassificationTest() {
        // given
        val mockClassificationList = listOf(
            Classification(
                id = 1L,
                type = ClassificationType.SUB_CLASSIFICATION,
                name = "subclassification_name",
                latitude = 1.0,
                longitude = 1.0
            )
        )
        given(classificationReader.findByType(ClassificationType.SUB_CLASSIFICATION))
            .willReturn(mockClassificationList)

        // when
        val result = classificationService.findAllSubClassification()

        // then
        assertAll(
            { assertEquals(1, result.subClassifications.size) },
            { assertEquals(1L, result.subClassifications[0].id) },
            { assertEquals("subclassification_name", result.subClassifications[0].name) }
        )
    }
}
