package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;
import upbrella.be.store.dto.request.UpdateStoreRequest;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.repository.BusinessHourRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BusinessHourService {

    private final BusinessHourRepository businessHourRepository;

    public void saveAllBusinessHour(List<BusinessHour> businessHours) {

        businessHourRepository.saveAll(businessHours);
    }

    public List<BusinessHour> findBusinessHourByStoreMetaId(Long storeMetaId) {

        return businessHourRepository.findByStoreMetaId(storeMetaId);
    }

    public List<BusinessHour> updateBusinessHour(long storeId, UpdateStoreRequest request) {

        List<BusinessHour> businessHours = findBusinessHourByStoreMetaId(storeId);
        List<SingleBusinessHourRequest> businessHoursRequest = request.getBusinessHours();
        for (int i = 0; i < businessHoursRequest.size(); i++) {
            businessHours.get(i).updateBusinessHour(businessHoursRequest.get(i));
        }
        return businessHours;
    }
}
