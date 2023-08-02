package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;
import upbrella.be.store.dto.request.UpdateStoreRequest;
import upbrella.be.store.dto.response.SingleBusinessHourResponse;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.repository.BusinessHourRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
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

    @Transactional
    public List<BusinessHour> updateBusinessHour(long storeId, UpdateStoreRequest request) {

        List<BusinessHour> businessHours = findBusinessHourByStoreMetaId(storeId);
        List<SingleBusinessHourRequest> businessHoursRequest = request.getBusinessHours();
        for (int i = 0; i < businessHoursRequest.size(); i++) {
            businessHours.get(i).updateBusinessHour(businessHoursRequest.get(i));
        }
        return businessHours;
    }

    public List<SingleBusinessHourResponse> createBusinessHourResponse(List<BusinessHour> businessHours) {

        return businessHours.stream()
                .map(SingleBusinessHourResponse::createSingleHourResponse)
                .sorted(Comparator.comparing(SingleBusinessHourResponse::getDate))
                .collect(Collectors.toList());
    }
}
