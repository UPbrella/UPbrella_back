package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.UpdateStoreRequest;
import upbrella.be.store.dto.response.*;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.exception.NonExistingStoreDetailException;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.umbrella.service.UmbrellaService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreDetailService {

    private final ClassificationService classificationService;
    private final UmbrellaService umbrellaService;
    private final StoreDetailRepository storeDetailRepository;
    private final BusinessHourService businessHourService;
    private final StoreImageService storeImageService;

    @Transactional
    @CacheEvict(value = "stores", key = "'allStores'")
    public void updateStore(Long storeId, UpdateStoreRequest request) {

        StoreDetail storeDetailById = findStoreDetailByStoreMetaId(storeId);

        Classification classification = classificationService.findClassificationById(request.getClassificationId());
        Classification subClassification = classificationService.findSubClassificationById(request.getSubClassificationId());

        StoreMeta storeMetaForUpdate = StoreMeta.createStoreMetaForUpdate(request, classification, subClassification);
        StoreMeta foundStoreMeta = storeDetailById.getStoreMeta();

        foundStoreMeta.updateStoreMeta(storeMetaForUpdate);
        storeDetailById.updateStore(foundStoreMeta, request);

        businessHourService.updateBusinessHours(foundStoreMeta, request.getBusinessHours());
    }

    @Transactional(readOnly = true)
    public StoreDetail findStoreDetailByStoreMetaId(Long storeId) {

        return storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeId)
                .orElseThrow(() -> new NonExistingStoreDetailException("[ERROR] 존재하지 않는 가게입니다."));
    }

    @Transactional
    public StoreFindByIdResponse findStoreDetailByStoreId(long storeId) {

        StoreDetail storeDetail = findStoreDetailByStoreMetaId(storeId);

        long availableUmbrellaCount = umbrellaService.countAvailableUmbrellaAtStore(storeId);

        return StoreFindByIdResponse.fromStoreDetail(storeDetail, availableUmbrellaCount);
    }

    @Transactional
    @Cacheable(value = "stores", key = "'allStores'")
    public List<SingleStoreResponse> findAllStores() {

        List<StoreDetail> storeDetails = storeDetailRepository.findAllStores();
        return storeDetails.stream()
                .map(this::createSingleStoreResponse)
                .collect(Collectors.toList());
    }

    private SingleStoreResponse createSingleStoreResponse(StoreDetail storeDetail) {

        return SingleStoreResponse.ofCreateSingleStoreResponse(storeDetail);
    }

    @Transactional
    public AllStoreIntroductionResponse findAllStoreIntroductions() {

        List<StoreDetail> storeDetails = storeDetailRepository.findAllStores();

        Map<Long, List<StoreDetail>> collected = storeDetails.stream()
                .collect(Collectors.groupingBy(storeDetail -> storeDetail.getStoreMeta().getSubClassification().getId()));

        //같은 ID끼리 리스트로 모은 것을 StoreIntroductionsResponseByClassification으로 변환
        List<StoreIntroductionsResponseByClassification> storeDetailsByClassification = collected.entrySet().stream()
                .sorted(Comparator.comparingLong(Map.Entry::getKey))
                .map(entry -> StoreIntroductionsResponseByClassification.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return AllStoreIntroductionResponse.of(storeDetailsByClassification);
    }

    @Transactional
    public void saveStoreDetail(StoreDetail storeDetail) {

        storeDetailRepository.save(storeDetail);
    }

    public StoreDetail findByStoreMetaId(Long storeId) {

        return storeDetailRepository.findStoreDetailByStoreMetaId(storeId)
                .orElseThrow(() -> new NonExistingStoreDetailException("[ERROR] 존재하지 않는 가게입니다."));
    }
}
