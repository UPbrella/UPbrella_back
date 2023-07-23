package upbrella.be.store.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
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
import upbrella.be.store.repository.ClassificationRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassificationServiceTest {

    @Mock
    private ClassificationRepository classificationRepository;
    @InjectMocks
    private ClassificationService classificationService;

    @Test
    @DisplayName("사용자는 대분류를 저장할 수 있다.")
    void createClassificationTest() {
        // given
        CreateClassificationRequest request = CreateClassificationRequest.builder()
                .type("대분류")
                .name("편의점")
                .latitude(37.1234)
                .longitude(127.1234)
                .build();
        Classification classification = Classification.createClassification(request);
        given(classificationRepository.save(any(Classification.class))).willReturn(classification);

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
                .type("소분류")
                .name("편의점")
                .build();
        Classification classification = Classification.createSubClassification(request);
        given(classificationRepository.save(any(Classification.class))).willReturn(classification);

        // when
        classificationService.createSubClassification(request);

        // then
        verify(classificationRepository, times(1)).save(any(Classification.class));
    }

    @Test
    @DisplayName("사용자는 분류를 삭제할 수 있다.")
    void deleteClassificationTest() {
        // given
        long classificationId = 1L;
        doNothing().when(classificationRepository).deleteById(classificationId);

        // when
        classificationService.deleteClassification(classificationId);

        // then
        verify(classificationRepository, times(1)).deleteById(classificationId);
    }

    @Test
    @DisplayName("사용자는 대분류를 조회할 수 있다.")
    void findAllClassificationTest() {
        // given
        String type = "classification";
        List<Classification> mockClassificationList = List.of(new Classification(1L, type, "classification_name", 1.0, 1.0));
        given(classificationRepository.findAllByClassification(type)).willReturn(mockClassificationList);

        // when
        AllClassificationResponse result = classificationService.findAllClassification(type);

        // then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getClassifications().size()).isEqualTo(1);
        softly.assertThat(result.getClassifications().get(0).getId()).isEqualTo(1L);
        softly.assertThat(result.getClassifications().get(0).getName()).isEqualTo("classification_name");
        softly.assertAll();
    }

    @Test
    @DisplayName("사용자는 소분류를 조회할 수 있다.")
    public void findAllSubClassificationTest() {
        // given
        String type = "subClassification";
        List<Classification> mockClassificationList = List.of(new Classification(1L, type, "subclassification_name", 1.0, 1.0));
        given(classificationRepository.findAllByClassification(type)).willReturn(mockClassificationList);

        // when
        AllSubClassificationResponse result = classificationService.findAllSubClassification(type);

        // then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result.getSubClassifications().size()).isEqualTo(1);
        softly.assertThat(result.getSubClassifications().get(0).getId()).isEqualTo(1L);
        softly.assertThat(result.getSubClassifications().get(0).getName()).isEqualTo("subclassification_name");
        softly.assertAll();
    }
}
