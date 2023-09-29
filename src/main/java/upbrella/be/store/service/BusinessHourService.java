package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;
import upbrella.be.store.dto.response.AllBusinessHourResponse;
import upbrella.be.store.dto.response.SingleBusinessHourResponse;
import upbrella.be.store.entity.BusinessHour;
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

    @Transactional
    public List<BusinessHour> updateBusinessHour(long storeId, List<SingleBusinessHourRequest> businessHourRequests) {

        List<BusinessHour> businessHours = findBusinessHourByStoreMetaId(storeId);

        for (int i = 0; i < businessHourRequests.size(); i++) {
            businessHours.get(i).updateBusinessHour(businessHourRequests.get(i));
        }
        return businessHours;
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
    public void createBusinessHour(Long storeId, SingleBusinessHourRequest businessHour) {

            BusinessHour newBusinessHour = BusinessHour.createBusinessHour(storeId, businessHour);
            businessHourRepository.save(newBusinessHour);
    }

    @Transactional
    public void deleteBusinessHour(Long businessHourId) {

        businessHourRepository.deleteById(businessHourId);
    }
}
