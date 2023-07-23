package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.dto.request.CreateClassificationRequest;
import upbrella.be.store.dto.request.CreateSubClassificationRequest;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.repository.ClassificationRepository;

@RequiredArgsConstructor
@Service
public class ClassificationService {

    private final ClassificationRepository classificationRepository;

    public void createClassification(CreateClassificationRequest request) {
        classificationRepository.save(Classification.createClassification(request));
    }

    public void createSubClassification(CreateSubClassificationRequest request) {
        classificationRepository.save(Classification.createSubClassification(request));
    }

    public void deleteClassification(Long id) {
        classificationRepository.deleteById(id);
    }
}
