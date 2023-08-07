package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.UpdateStoreRequest;
import upbrella.be.store.dto.response.SingleBusinessHourResponse;
import upbrella.be.store.dto.response.SingleImageUrlResponse;
import upbrella.be.store.dto.response.SingleStoreResponse;
import upbrella.be.store.dto.response.StoreFindByIdResponse;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.umbrella.service.UmbrellaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreDetailService {

    private final ClassificationService classificationService;
    private final StoreMetaService storeMetaService;
    private final UmbrellaService umbrellaService;
    private final StoreDetailRepository storeDetailRepository;
    private final BusinessHourService businessHourService;
    private final StoreImageService storeImageService;

    @Transactional
    public void updateStore(Long storeId, UpdateStoreRequest request) {

        Classification classification = classificationService.findClassificationById(request.getClassificationId());
        Classification subClassification = classificationService.findSubClassificationById(request.getSubClassificationId());

        List<BusinessHour> businessHours = businessHourService.updateBusinessHour(storeId, request.getBusinessHours());

        StoreMeta storeMetaForUpdate = StoreMeta.createStoreMetaForUpdate(request, classification, subClassification, businessHours);

        StoreMeta foundStoreMeta = storeMetaService.findStoreMetaById(storeId);
        foundStoreMeta.updateStoreMeta(storeMetaForUpdate);

        StoreDetail storeDetailById = findStoreDetailById(storeId);
        storeDetailById.updateStore(foundStoreMeta, request);
    }

    @Transactional(readOnly = true)
    public StoreDetail findStoreDetailById(Long storeId) {

        return storeDetailRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 가게입니다."));
    }

    @Transactional
    public StoreFindByIdResponse findStoreDetailByStoreMetaId(long storeMetaId) {

        StoreDetail storeDetail = storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeMetaId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당하는 협업 지점이 존재하지 않습니다."));

        int availableUmbrellaCount = umbrellaService.countAvailableUmbrellaAtStore(storeMetaId);

        return StoreFindByIdResponse.fromStoreDetail(storeDetail, availableUmbrellaCount);
    }

    @Transactional
    public List<SingleStoreResponse> findAllStores() {

        List<StoreDetail> storeDetails = storeDetailRepository.findAllStores();
        return storeDetails.stream()
                .map(this::createSingleStoreResponse)
                .collect(Collectors.toList());
    }

    public SingleStoreResponse createSingleStoreResponse(StoreDetail storeDetail) {

        List<SingleImageUrlResponse> imageUrls = storeImageService.createImageUrlResponse(storeDetail);
        String thumbnail = storeImageService.createThumbnail(imageUrls);
        Set<BusinessHour> businessHourSet = storeDetail.getStoreMeta().getBusinessHours();
        List<BusinessHour> businessHourList = new ArrayList<>(businessHourSet);
        List<SingleBusinessHourResponse> businessHours = businessHourService.createBusinessHourResponse(businessHourList);
        return SingleStoreResponse.ofCreateSingleStoreResponse(storeDetail, thumbnail, imageUrls, businessHours);
    }
}
