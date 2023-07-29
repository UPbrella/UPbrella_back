package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.dto.request.CreateClassificationRequest;
import upbrella.be.store.dto.request.CreateSubClassificationRequest;
import upbrella.be.store.dto.response.AllClassificationResponse;
import upbrella.be.store.dto.response.AllSubClassificationResponse;
import upbrella.be.store.dto.response.SingleClassificationResponse;
import upbrella.be.store.dto.response.SingleSubClassificationResponse;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.repository.ClassificationRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ClassificationService {

    private final ClassificationRepository classificationRepository;

    public Classification createClassification(CreateClassificationRequest request) {
        return classificationRepository.save(Classification.ofCreateClassification(request));
    }

    public Classification createSubClassification(CreateSubClassificationRequest request) {
        return classificationRepository.save(Classification.ofCreateSubClassification(request));
    }

    public void deleteClassification(Long id) {
        classificationRepository.deleteById(id);
    }

    public AllClassificationResponse findAllClassification(String type) {
        List<Classification> allByClassification = classificationRepository.findByType(type);
        List<SingleClassificationResponse> classifications = new ArrayList<>();

        for (Classification classification : allByClassification) {
            classifications.add(SingleClassificationResponse.ofCreateClassification(classification));
        }

        return AllClassificationResponse.builder()
                .classifications(classifications)
                .build();
    }

    public AllSubClassificationResponse findAllSubClassification(String type) {
        List<Classification> allByClassification = classificationRepository.findByType(type);
        List<SingleSubClassificationResponse> classifications = new ArrayList<>();

        for (Classification classification : allByClassification) {
            classifications.add(SingleSubClassificationResponse.ofCreateSubClassification(classification));
        }

        return AllSubClassificationResponse.builder()
                .subClassifications(classifications)
                .build();
    }
}