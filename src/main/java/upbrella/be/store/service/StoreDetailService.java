package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;
import upbrella.be.store.dto.response.StoreFindByIdResponse;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.umbrella.service.UmbrellaService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreDetailService {

    private final ClassificationService classificationService;
    private final StoreMetaService storeMetaService;
    private final UmbrellaService umbrellaService;
    private final StoreDetailRepository storeDetailRepository;
    private final BusinessHourService businessHourService;


    @Transactional
    public void updateStore(Long storeId, CreateStoreRequest request) {

        // private 메서드로 classification 업데이트
        Classification classification = classificationService.findClassificationById(request.getClassificationId());
        Classification subClassification = classificationService.findSubClassificationById(request.getSubClassificationId());

        // private 메서드로 businessHours 업데이트
        List<BusinessHour> businessHours = businessHourService.findBusinessHourByStoreMetaId(storeId);
        List<SingleBusinessHourRequest> businessHoursRequest = request.getBusinessHours();
        for (int i = 0; i < businessHoursRequest.size(); i++) {
            businessHours.get(i).updateBusinessHour(businessHoursRequest.get(i));
        }

        StoreMeta storeMetaForUpdate = StoreMeta.createStoreMetaForUpdate(request, classification, subClassification, businessHours);

        StoreMeta foundStoreMeta = storeMetaService.findStoreMetaById(storeId);
        foundStoreMeta.updateStoreMeta(storeMetaForUpdate);

        StoreDetail storeDetailById = findStoreDetailById(storeId);

        storeDetailById.updateStore(foundStoreMeta, request);
    }

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
}
