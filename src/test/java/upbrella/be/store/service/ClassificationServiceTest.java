package upbrella.be.store.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.dto.request.CreateClassificationRequest;
import upbrella.be.store.dto.request.CreateSubClassificationRequest;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.repository.ClassificationRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
