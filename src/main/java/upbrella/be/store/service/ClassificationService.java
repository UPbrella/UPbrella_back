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

    public void createClassification(CreateClassificationRequest request) {
        classificationRepository.save(Classification.createClassification(request));
    }

    public void createSubClassification(CreateSubClassificationRequest request) {
        classificationRepository.save(Classification.createSubClassification(request));
    }

    public void deleteClassification(Long id) {
        classificationRepository.deleteById(id);
    }

    public AllClassificationResponse findAllClassification(String type) {
        List<Classification> allByClassification = classificationRepository.findAllByClassification(type);
        List<SingleClassificationResponse> classifications = new ArrayList<>();

        for (Classification classification : allByClassification) {
            classifications.add(SingleClassificationResponse.builder()
                    .id(classification.getId())
                    .name(classification.getName())
                    .latitude(classification.getLatitude())
                    .longitude(classification.getLongitude())
                    .build());
        }

        return AllClassificationResponse.builder()
                .classifications(classifications)
                .build();
    }

    public AllSubClassificationResponse findAllSubClassification(String type) {
        List<Classification> allByClassification = classificationRepository.findAllByClassification(type);
        List<SingleSubClassificationResponse> classifications = new ArrayList<>();

        for (Classification classification : allByClassification) {
            classifications.add(SingleSubClassificationResponse.builder()
                    .id(classification.getId())
                    .name(classification.getName())
                    .build());
        }

        return AllSubClassificationResponse.builder()
                .subClassifications(classifications)
                .build();
    }
}
