package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;
import upbrella.be.store.dto.response.AllBusinessHourResponse;
import upbrella.be.store.dto.response.SingleBusinessHourResponse;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.repository.BusinessHourRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessHourService {

    private final BusinessHourRepository businessHourRepository;

    @Transactional
    public void saveAllBusinessHour(List<BusinessHour> businessHours) {

        businessHourRepository.saveAll(businessHours);
    }

    @Transactional(readOnly = true)
    public List<BusinessHour> findBusinessHourByStoreMetaId(Long storeMetaId) {

        return businessHourRepository.findByStoreMetaId(storeMetaId);
    }

    public List<SingleBusinessHourResponse> createBusinessHourResponse(List<BusinessHour> businessHours) {

        return businessHours.stream()
                .map(SingleBusinessHourResponse::createSingleHourResponse)
                .sorted(Comparator.comparing(SingleBusinessHourResponse::getDate))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AllBusinessHourResponse findAllBusinessHours(Long storeId) {

        List<BusinessHour> businessHours = findBusinessHourByStoreMetaId(storeId);
        return AllBusinessHourResponse.builder()
                .businessHours(createBusinessHourResponse(businessHours))
                .build();
    }

    @Transactional
    public void updateBusinessHours(StoreMeta storeMeta, List<SingleBusinessHourRequest> businessHoursRequest) {

        businessHourRepository.deleteAllByStoreMetaId(storeMeta.getId());

        List<BusinessHour> businessHours = businessHoursRequest.stream()
                .map(businessHourRequest -> BusinessHour.ofCreateBusinessHour(businessHourRequest, storeMeta))
                .collect(Collectors.toList());

        businessHourRepository.saveAll(businessHours);
    }
}
