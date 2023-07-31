package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.AllBusinessHourRequest;
import upbrella.be.store.dto.request.CoordinateRequest;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse;
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.store.repository.StoreImageRepository;
import upbrella.be.store.repository.StoreMetaRepository;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreMetaService {

    private final UmbrellaRepository umbrellaRepository;
    private final StoreMetaRepository storeMetaRepository;
    private final StoreDetailRepository storeDetailRepository;
    private final StoreImageRepository storeImageRepository;
    private final ClassificationService classificationService;
    private final BusinessHourService businessHourService;

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
    }

    @Transactional
    public void deleteStoreMeta(long storeMetaId) {

        storeMetaRepository.findById(storeMetaId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."))
                .delete();
    }

    @Transactional(readOnly = true)
    public StoreMeta findStoreMetaById(long id) {

        return storeMetaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));
    }

    private StoreMeta saveStoreMeta(CreateStoreRequest store) {

        Classification classification = classificationService.findClassificationById(store.getClassificationId());
        Classification subClassification = classificationService.findSubClassificationById(store.getSubClassificationId());

        AllBusinessHourRequest businessHoursRequest = store.getBusinessHours();
        List<BusinessHour> businessHours = new ArrayList<>();

        StoreMeta storeMeta = storeMetaRepository.save(StoreMeta.createStoreMetaForSave(store, classification, subClassification));

        businessHoursRequest.getBusinessHours().forEach(businessHourRequest -> {
            businessHours.add(BusinessHour.ofCreateBusinessHour(businessHourRequest, storeMeta));
        });
        businessHourService.saveAllBusinessHour(businessHours);

        return storeMeta;
    }

    private StoreDetail saveStoreDetail(CreateStoreRequest store, StoreMeta storeMeta) {

        return storeDetailRepository.save(StoreDetail.createForSave(store, storeMeta));
    }
}
