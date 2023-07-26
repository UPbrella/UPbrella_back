package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;
import upbrella.be.store.StoreRepository.StoreMetaRepository;

import upbrella.be.store.dto.request.CoordinateRequest;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreImage;
import upbrella.be.store.repository.ClassificationRepository;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.store.repository.StoreImageRepository;
import upbrella.be.store.repository.StoreMetaRepository;
import upbrella.be.store.entity.StoreMeta;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreMetaService {

    private final UmbrellaRepository umbrellaRepository;
    private final StoreMetaRepository storeMetaRepository;

    @Transactional(readOnly = true)
    public CurrentUmbrellaStoreResponse findCurrentStoreIdByUmbrella(long umbrellaId) {

        Umbrella foundUmbrella = umbrellaRepository.findByIdAndDeletedIsFalse(umbrellaId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 우산입니다."));

        if (foundUmbrella.getStoreMeta().isDeleted()) {
            throw new IllegalArgumentException("[ERROR] 삭제된 가게입니다.");
        }
        return CurrentUmbrellaStoreResponse.fromUmbrella(foundUmbrella);
    }

    public List<SingleCurrentLocationStoreResponse> findStoresInCurrentMap(CoordinateRequest coordinateRequest) {

        List<StoreMeta> storeMetaListInCurrentMap = storeMetaRepository.findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(
                coordinateRequest.getLatitudeFrom(), coordinateRequest.getLatitudeTo(),
                coordinateRequest.getLongitudeFrom(), coordinateRequest.getLongitudeTo()
        );

        return storeMetaListInCurrentMap.stream()
                .map(SingleCurrentLocationStoreResponse::fromStoreMeta)
                .collect(Collectors.toUnmodifiableList());
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

