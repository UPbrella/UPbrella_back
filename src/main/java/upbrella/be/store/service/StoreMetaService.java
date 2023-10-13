package upbrella.be.store.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;
import upbrella.be.store.dto.response.AllCurrentLocationStoreResponse;
import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse;
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse;
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount;
import upbrella.be.store.entity.*;
import upbrella.be.store.exception.DeletedStoreDetailException;
import upbrella.be.store.exception.EssentialImageException;
import upbrella.be.store.exception.NonExistingStoreMetaException;
import upbrella.be.store.repository.StoreMetaRepository;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.exception.NonExistingUmbrellaException;
import upbrella.be.umbrella.repository.UmbrellaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StoreMetaService {

    private final UmbrellaRepository umbrellaRepository;
    private final StoreMetaRepository storeMetaRepository;
    private final StoreDetailService storeDetailService;
    private final ClassificationService classificationService;
    private final BusinessHourService businessHourService;

    public StoreMetaService(UmbrellaRepository umbrellaRepository, StoreMetaRepository storeMetaRepository, @Lazy StoreDetailService storeDetailService, ClassificationService classificationService, BusinessHourService businessHourService) {

        this.umbrellaRepository = umbrellaRepository;
        this.storeMetaRepository = storeMetaRepository;
        this.storeDetailService = storeDetailService;
        this.classificationService = classificationService;
        this.businessHourService = businessHourService;
    }

    @Transactional(readOnly = true)
    public CurrentUmbrellaStoreResponse findCurrentStoreIdByUmbrella(long umbrellaId) {

        Umbrella foundUmbrella = umbrellaRepository.findByIdAndDeletedIsFalse(umbrellaId)
                .orElseThrow(() -> new NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산입니다."));

        if (foundUmbrella.getStoreMeta().isDeleted()) {
            throw new DeletedStoreDetailException("[ERROR] 삭제된 가게입니다.");
        }
        return CurrentUmbrellaStoreResponse.fromUmbrella(foundUmbrella);
    }

    @Transactional(readOnly = true)
    public AllCurrentLocationStoreResponse findAllStoresByClassification(long classificationId, LocalDateTime currentTime) {

        List<StoreMetaWithUmbrellaCount> storeMetaWithUmbrellaCounts = storeMetaRepository.findAllStoresByClassification(classificationId);
        
        return AllCurrentLocationStoreResponse.ofCreate(
                storeMetaWithUmbrellaCounts.stream()
                        .map(storeMetaWithUmbrellaCount -> mapToSingleCurrentLocationStoreResponse(storeMetaWithUmbrellaCount, currentTime))
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    @CacheEvict(value = "stores", key = "'allStores'")
    public void createStore(CreateStoreRequest store) {

        StoreMeta storeMeta = saveStoreMeta(store);
        saveStoreDetail(store, storeMeta);
    }

    @Transactional
    @CacheEvict(value = "stores", key = "'allStores'")
    public void deleteStoreMeta(long storeMetaId) {

        findStoreMetaById(storeMetaId).delete();
    }

    @Transactional(readOnly = true)
    public StoreMeta findStoreMetaById(long id) {

        return storeMetaRepository.findById(id)
                .orElseThrow(() -> new NonExistingStoreMetaException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다."));
    }

    @Transactional(readOnly = true)
    public boolean existByStoreId(long storeId) {

        return storeMetaRepository.existsById(storeId);
    }

    @Transactional(readOnly = true)
    public boolean existByClassificationId(long classificationId) {

        return storeMetaRepository.existsByClassificationId(classificationId);
    }

    @Transactional
    @CacheEvict(value = "stores", key = "'allStores'")
    public void activateStoreStatus(long storeId) {

        StoreDetail storeDetail = storeDetailService.findStoreDetailByStoreMetaId(storeId);

        List<StoreImage> storeImages = storeDetail.getStoreImages();
        if (storeImages == null || storeImages.isEmpty()) {
            throw new EssentialImageException("[ERROR] 가게 이미지가 존재하지 않으면 영업지점을 활성화할 수 없습니다.");
        }

        storeDetail.getStoreMeta().activateStoreStatus();
    }

    @Transactional
    @CacheEvict(value = "stores", key = "'allStores'")
    public void inactivateStoreStatus(long storeId) {

        StoreDetail storeDetail = storeDetailService.findStoreDetailByStoreMetaId(storeId);

        storeDetail.getStoreMeta().inactivateStoreStatus();
    }

    private boolean isOpenStore(StoreMetaWithUmbrellaCount storeMetaWithUmbrellaCount, LocalDateTime currentTime) {

        List<BusinessHour> businessHours = storeMetaWithUmbrellaCount.getStoreMeta().getBusinessHours();

        return businessHours.stream()
                .filter(businessHour -> businessHour.getDate().equals(currentTime.getDayOfWeek()))
                .filter(e -> storeMetaWithUmbrellaCount.getStoreMeta().isActivated())
                .anyMatch(businessHour ->
                        currentTime.toLocalTime().isAfter(businessHour.getOpenAt())
                                && currentTime.toLocalTime().isBefore(businessHour.getCloseAt()));
    }

    private SingleCurrentLocationStoreResponse mapToSingleCurrentLocationStoreResponse(StoreMetaWithUmbrellaCount storeMetaWithUmbrellaCount, LocalDateTime currentTime) {

        return SingleCurrentLocationStoreResponse.fromStoreMeta(
                isOpenStore(storeMetaWithUmbrellaCount, currentTime), storeMetaWithUmbrellaCount
        );
    }

    private void saveStoreDetail(CreateStoreRequest store, StoreMeta storeMeta) {

        storeDetailService.saveStoreDetail(StoreDetail.createForSave(store, storeMeta));
    }

    private StoreMeta saveStoreMeta(CreateStoreRequest store) {

        Classification classification = classificationService.findClassificationById(store.getClassificationId());
        Classification subClassification = classificationService.findSubClassificationById(store.getSubClassificationId());

        List<SingleBusinessHourRequest> businessHourRequests = store.getBusinessHours();

        StoreMeta storeMeta = storeMetaRepository.save(StoreMeta.createStoreMetaForSave(store, classification, subClassification));

        List<BusinessHour> businessHours = businessHourRequests.stream()
                .map(businessHourRequest -> BusinessHour.ofCreateBusinessHour(businessHourRequest, storeMeta))
                .collect(Collectors.toUnmodifiableList());

        businessHourService.saveAllBusinessHour(businessHours);

        return storeMeta;
    }
}
