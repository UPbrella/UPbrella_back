package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.response.StoreFindByIdResponse;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.umbrella.service.UmbrellaService;


@RequiredArgsConstructor
@Service
public class StoreDetailService {

    private final ClassificationService classificationService;
    private final StoreImageService storeImageService;
    private final StoreMetaService storeMetaService;
    private final UmbrellaService umbrellaService;
    private final StoreDetailRepository storeDetailRepository;


    @Transactional
    public void updateStore(Long storeId, CreateStoreRequest request) {

        Classification classification = classificationService.findClassificationById(request.getClassificationId());
        Classification subClassification = classificationService.findSubClassificationById(request.getSubClassificationId());

        StoreMeta storeMetaForUpdate = StoreMeta.createStoreMetaForSave(request, classification, subClassification);
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
