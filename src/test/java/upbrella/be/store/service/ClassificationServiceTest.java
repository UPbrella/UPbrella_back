package upbrella.be.store.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.dto.request.CreateClassificationRequest;
import upbrella.be.store.dto.request.CreateSubClassificationRequest;
import upbrella.be.store.dto.response.AllClassificationResponse;
import upbrella.be.store.dto.response.AllSubClassificationResponse;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;
import upbrella.be.store.exception.AssignedClassificationException;
import upbrella.be.store.exception.IncorrectClassificationException;
import upbrella.be.store.repository.ClassificationRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassificationServiceTest {

    @Mock
    private ClassificationRepository classificationRepository;
    @Mock
    private StoreMetaService storeMetaService;
    @InjectMocks
    private ClassificationService classificationService;

    @Test
    @DisplayName("사용자는 대분류를 저장할 수 있다.")
    void createClassificationTest() {
        // given
        CreateClassificationRequest request = CreateClassificationRequest.builder()
                .name("편의점")
                .latitude(37.1234)
                .longitude(127.1234)
                .build();
        given(classificationRepository.save(any(Classification.class))).willReturn(Classification.builder().build());

        // when
        classificationService.createClassification(request);

        // then
        verify(classificationRepository, times(1)).save(any(Classification.class));
    }

    @Test
    @DisplayName("사용자는 소분류를 저장할 수 있다.")
    void createSubClassificationTest() {
        // given
        CreateSubClassificationRequest request = CreateSubClassificationRequest.builder()
                .name("편의점")
                .build();

        given(classificationRepository.save(any(Classification.class))).willReturn(Classification.builder().build());

        // when
        classificationService.createSubClassification(request);

        // then
        verify(classificationRepository, times(1)).save(any(Classification.class));
    }

    @Test
    @DisplayName("사용자는 대분류를 삭제할 수 있다.")
    void deleteClassificationTest() {
        // given
        long classificationId = 1L;
        given(storeMetaService.existByClassificationId(classificationId)).willReturn(false);
        given(classificationRepository.existsById(classificationId)).willReturn(true);
        doNothing().when(classificationRepository).deleteById(classificationId);

        // when
        classificationService.deleteClassification(classificationId);

        // then
        verify(classificationRepository, times(1)).deleteById(classificationId);
    }

    @Test
    @DisplayName("사용자가 할당된 대분류를 삭제하려고 하면 예외를 발생시킨다.")
    void unableToDeleteClassification() {
        // given
        long classificationId = 1L;
        given(storeMetaService.existByClassificationId(classificationId)).willReturn(true);
        given(classificationRepository.existsById(classificationId)).willReturn(true);

        // when


        // then
        assertThatThrownBy(() -> classificationService.deleteClassification(classificationId))
                .isInstanceOf(AssignedClassificationException.class)
                .hasMessageContaining("[ERROR] 해당 대분류에 속한 협업지점이 존재합니다.");
    }

    @Test
    @DisplayName("사용자가 할당된 소분류를 삭제하려고 하면 예외를 발생시킨다.")
    void unableToDeleteSubClassification() {
        // given
        long classificationId = 1L;
        given(storeMetaService.existByClassificationId(classificationId)).willReturn(true);
        given(classificationRepository.existsById(classificationId)).willReturn(true);

        // when


        // then
        assertThatThrownBy(() -> classificationService.deleteSubClassification(classificationId))
                .isInstanceOf(AssignedClassificationException.class)
                .hasMessageContaining("[ERROR] 해당 소분류에 속한 협업지점이 존재합니다.");
    }

    @Test
    @DisplayName("사용자는 대분류를 조회할 수 있다.")
    void findAllClassificationTest() {
        // given
        List<Classification> mockClassificationList = List.of(new Classification(1L, ClassificationType.CLASSIFICATION, "classification_name", 1.0, 1.0));
        given(classificationRepository.findByType(ClassificationType.CLASSIFICATION)).willReturn(mockClassificationList);

        // when
        AllClassificationResponse result = classificationService.findAllClassification();

        // then
        assertAll(
                () -> assertEquals(1, result.getClassifications().size()),
                () -> assertEquals(1L, result.getClassifications().get(0).getId()),
                () -> assertEquals("classification_name", result.getClassifications().get(0).getName())
        );
    }

    @Nested
    @DisplayName("사용자는 대분류를 ")
    class ClassificationTest {

        Classification classification = Classification.builder()
                .id(1L)
                .type(ClassificationType.CLASSIFICATION)
                .name("classification_name")
                .latitude(1.0)
                .longitude(1.0)
                .build();

        Classification subClassification = Classification.builder()
                .id(1L)
                .type(ClassificationType.SUB_CLASSIFICATION)
                .name("sub_classification_name")
                .build();

        @Test
        @DisplayName("id로 조회할 수 있다.")
        void findClassificationById() {
            // given
            long classificationId = 1L;
            given(classificationRepository.findById(classificationId)).willReturn(Optional.ofNullable(classification));

            // when
            Classification foundClassification = classificationService.findClassificationById(classificationId);

            // then
            assertAll(
                    () -> assertThat(foundClassification).isNotNull(),
                    () -> assertEquals(1L, foundClassification.getId()),
                    () -> assertEquals("classification_name", foundClassification.getName())
            );
        }

        @Test
        @DisplayName("조회했는데 소분류가 조회되면 예외를 발생시킨다.")
        void test() {
            // given
            long classificationId = 1L;
            given(classificationRepository.findById(classificationId)).willReturn(Optional.ofNullable(subClassification));


            // then
            assertThatThrownBy(() -> classificationService.findClassificationById(classificationId))
                    .isInstanceOf(IncorrectClassificationException.class)
                    .hasMessage("[ERROR] Classification이 아닙니다.");
        }
    }

    @Test
    @DisplayName("사용자는 소분류를 조회할 수 있다.")
    void findAllSubClassificationTest() {
        // given
        List<Classification> mockClassificationList = List.of(new Classification(1L, ClassificationType.SUB_CLASSIFICATION, "subclassification_name", 1.0, 1.0));
        given(classificationRepository.findByType(ClassificationType.SUB_CLASSIFICATION)).willReturn(mockClassificationList);

        // when
        AllSubClassificationResponse result = classificationService.findAllSubClassification();

        // then
        assertAll(
                () -> assertEquals(1, result.getSubClassifications().size()),
                () -> assertEquals(1L, result.getSubClassifications().get(0).getId()),
                () -> assertEquals("subclassification_name", result.getSubClassifications().get(0).getName())
        );
    }

    @Nested
    @DisplayName("사용자는 소분류를 ")
    class SubClassificationTest {
        Classification classification = Classification.builder()
                .id(1L)
                .type(ClassificationType.CLASSIFICATION)
                .name("classification_name")
                .latitude(1.0)
                .longitude(1.0)
                .build();

        Classification subClassification = Classification.builder()
                .id(1L)
                .type(ClassificationType.SUB_CLASSIFICATION)
                .name("sub_classification_name")
                .build();

        @Test
        @DisplayName("id로 조회할 수 있다.")
        void findClassificationById() {
            // given
            long subClassificationId = 1L;
            given(classificationRepository.findById(subClassificationId)).willReturn(Optional.ofNullable(subClassification));

            // when
            Classification foundSubClassification = classificationService.findSubClassificationById(subClassificationId);

            // then
            assertAll(
                    () -> assertThat(foundSubClassification).isNotNull(),
                    () -> assertEquals(1L, foundSubClassification.getId()),
                    () -> assertEquals("sub_classification_name", foundSubClassification.getName())
            );
        }

        @Test
        @DisplayName("조회했는데 소분류가 조회되면 예외를 발생시킨다.")
        void test() {
            // given
            long classificationId = 1L;
            given(classificationRepository.findById(classificationId)).willReturn(Optional.ofNullable(classification));


            // then
            assertThatThrownBy(() -> classificationService.findSubClassificationById(classificationId))
                    .isInstanceOf(IncorrectClassificationException.class)
                    .hasMessage("[ERROR] SubClassification이 아닙니다.");
        }
    }
}