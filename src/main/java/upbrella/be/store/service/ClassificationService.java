package upbrella.be.store.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.CreateClassificationRequest;
import upbrella.be.store.dto.request.CreateSubClassificationRequest;
import upbrella.be.store.dto.response.AllClassificationResponse;
import upbrella.be.store.dto.response.AllSubClassificationResponse;
import upbrella.be.store.dto.response.SingleClassificationResponse;
import upbrella.be.store.dto.response.SingleSubClassificationResponse;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;
import upbrella.be.store.exception.AssignedClassificationException;
import upbrella.be.store.exception.IncorrectClassificationException;
import upbrella.be.store.exception.NonExistingClassificationException;
import upbrella.be.store.repository.ClassificationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClassificationService {

    private final ClassificationRepository classificationRepository;
    private final StoreMetaService storeMetaService;

    public ClassificationService(ClassificationRepository classificationRepository, @Lazy StoreMetaService storeMetaService) {
        this.classificationRepository = classificationRepository;
        this.storeMetaService = storeMetaService;
    }

    public Classification createClassification(CreateClassificationRequest request) {

        return classificationRepository.save(Classification.ofCreateClassification(request));
    }

    public Classification createSubClassification(CreateSubClassificationRequest request) {

        return classificationRepository.save(Classification.ofCreateSubClassification(request));
    }

    @Transactional
    public void deleteClassification(Long id) {

        if (!classificationRepository.existsById(id)) {
            throw new NonExistingClassificationException("[ERROR] 존재하지 않는 대분류입니다.");
        }

        if (storeMetaService.existByClassificationId(id)) {
            throw new AssignedClassificationException("[ERROR] 해당 대분류에 속한 협업지점이 존재합니다.");
        }

        classificationRepository.deleteById(id);
    }

    public void deleteSubClassification(Long id) {

        if (!classificationRepository.existsById(id)) {
            throw new NonExistingClassificationException("[ERROR] 존재하지 않는 소분류입니다.");
        }

        if (storeMetaService.existByClassificationId(id)) {
            throw new AssignedClassificationException("[ERROR] 해당 소분류에 속한 협업지점이 존재합니다.");
        }

        classificationRepository.deleteById(id);
    }

    public AllClassificationResponse findAllClassification() {

        List<Classification> allByClassification = classificationRepository.findByType(ClassificationType.CLASSIFICATION);
        List<SingleClassificationResponse> classifications = new ArrayList<>();

        for (Classification classification : allByClassification) {
            classifications.add(SingleClassificationResponse.ofCreateClassification(classification));
        }

        return AllClassificationResponse.builder()
                .classifications(classifications)
                .build();
    }

    public AllSubClassificationResponse findAllSubClassification() {

        List<Classification> allByClassification = classificationRepository.findByType(ClassificationType.SUB_CLASSIFICATION);
        List<SingleSubClassificationResponse> classifications = new ArrayList<>();

        for (Classification classification : allByClassification) {
            classifications.add(SingleSubClassificationResponse.ofCreateSubClassification(classification));
        }

        return AllSubClassificationResponse.builder()
                .subClassifications(classifications)
                .build();
    }

    public Classification findClassificationById(Long id) {

        Classification classification = findClassificationEntityById(id);
        if (!classification.getType().equals(ClassificationType.CLASSIFICATION)) {
            throw new IncorrectClassificationException("[ERROR] Classification이 아닙니다.");
        }
        return classification;
    }

    public Classification findSubClassificationById(Long id) {

        Classification classification = findClassificationEntityById(id);
        if (!classification.getType().equals(ClassificationType.SUB_CLASSIFICATION)) {
            throw new IncorrectClassificationException("[ERROR] SubClassification이 아닙니다.");
        }
        return classification;
    }

    private Classification findClassificationEntityById(long id) {

        return classificationRepository.findById(id)
                .orElseThrow(() -> new NonExistingClassificationException("[ERROR] 존재하지 않는 분류입니다."));
    }
}
