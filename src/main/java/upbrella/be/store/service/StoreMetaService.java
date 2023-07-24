package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.dto.request.ClassificationRequest;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.SubClassificationRequest;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreImage;
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

    public StoreMeta findById(long id) {
        return storeMetaRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));
    }

    public void createStore(CreateStoreRequest store) {

        StoreMeta storeMeta = saveStoreMeta(store);
        StoreDetail storeDetail = saveStoreDetail(store, storeMeta);
        saveStoreImage(store.getImageUrls(), storeDetail);
    }

    private StoreMeta saveStoreMeta(CreateStoreRequest store) {
        return storeMetaRepository.save(createForSave(store));
    }

    private StoreDetail saveStoreDetail(CreateStoreRequest store, StoreMeta storeMeta) {
        return storeDetailRepository.save(StoreDetail.createForSave(store, storeMeta));
    }

    private void saveStoreImage(List<String> urls, StoreDetail storeDetail) {

        for (String imageUrl : urls) {
            storeImageRepository.save(StoreImage.createStoreImage(storeDetail, imageUrl));
        }
    }

    private StoreMeta createForSave(CreateStoreRequest request){
        return StoreMeta.builder()
                .name(request.getName())
                .thumbnail(request.getImageUrls().get(0))
                .activated(request.isActivateStatus())
                .deleted(false)
                .classification(createClassification(request.getClassification()))
                .subClassification(createSubClassification(request.getSubClassification()))
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    private Classification createClassification(ClassificationRequest classification) {
        return Classification.builder()
                .type(classification.getType())
                .name(classification.getName())
                .latitude(classification.getLatitude())
                .longitude(classification.getLongitude())
                .build();
    }

    public Classification createSubClassification(SubClassificationRequest subClassification) {
        return Classification.builder()
                .type(subClassification.getType())
                .name(subClassification.getName())
                .build();
    }
}
