package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreImage;
import upbrella.be.store.repository.ClassificationRepository;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.store.repository.StoreImageRepository;
import upbrella.be.store.repository.StoreMetaRepository;
import upbrella.be.store.entity.StoreMeta;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreMetaService {

    private final StoreMetaRepository storeMetaRepository;
    private final StoreDetailRepository storeDetailRepository;
    private final StoreImageRepository storeImageRepository;
    private final ClassificationRepository classificationRepository;
    private final StoreImageService storeImageService;

    @Transactional(readOnly = true)
    public StoreMeta findStoreMetaById(long id) {
        return storeMetaRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));
    }

    @Transactional
    public void createStore(CreateStoreRequest store) {

        StoreMeta storeMeta = saveStoreMeta(store);
        StoreDetail storeDetail = saveStoreDetail(store, storeMeta);
        saveStoreImage(store.getImageUrls(), storeDetail);
    }

    @Transactional
    public void updateStore(long id, CreateStoreRequest store) {

    }

    @Transactional
    public void deleteStoreMeta(long storeMetaId) {
        storeMetaRepository.findById(storeMetaId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."))
                .delete();
    }

    private StoreMeta saveStoreMeta(CreateStoreRequest store) {
        return storeMetaRepository.save(createStoreMetaForSave(store));
    }

    private StoreDetail saveStoreDetail(CreateStoreRequest store, StoreMeta storeMeta) {
        return storeDetailRepository.save(StoreDetail.createForSave(store, storeMeta));
    }

    private void saveStoreImage(List<String> urls, StoreDetail storeDetail) {

        for (String imageUrl : urls) {
            storeImageRepository.save(StoreImage.createStoreImage(storeDetail, imageUrl));
        }
    }

    private StoreMeta createStoreMetaForSave(CreateStoreRequest request) {

        Classification classification = classificationRepository.findById(request.getClassificationId())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 분류 고유번호입니다."));

        Classification subClassification = classificationRepository.findById(request.getSubClassificationId())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 분류 고유번호입니다."));

        return StoreMeta.builder()
                .name(request.getName())
                .thumbnail(request.getImageUrls().get(0))
                .activated(request.isActivateStatus())
                .deleted(false)
                .classification(classification)
                .subClassification(subClassification)
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    private StoreDetail createStoreDetailForUpdate(CreateStoreRequest store, StoreMeta storeMeta) {

        return StoreDetail.builder()
                .storeMeta(storeMeta)
                .address(store.getAddress())
                .umbrellaLocation(store.getUmbrellaLocation())
                .workingHour(store.getBusinessHours())
                .contactInfo(store.getContactNumber())
                .instaUrl(store.getInstagramId())
                .content(store.getContent())
                .build();
    }
}
