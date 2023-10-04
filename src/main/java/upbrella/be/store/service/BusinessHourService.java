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

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        // 요일별로 BusinessHour와 SingleBusinessHourRequest 인덱싱
        List<BusinessHour> businessHours = storeMeta.getBusinessHours();
        Map<DayOfWeek, BusinessHour> businessHourMap = businessHours.stream()
                .collect(Collectors.toMap(BusinessHour::getDate, businessHour -> businessHour));

        Map<DayOfWeek, SingleBusinessHourRequest> requestMap = businessHoursRequest.stream()
                .collect(Collectors.toMap(SingleBusinessHourRequest::getDate, request -> request));

        for (DayOfWeek day : DayOfWeek.values()) {

            // 해당하는 요일에 BusinessHour가 둘 다 존재하는 경우
            if (businessHourMap.get(day) != null && requestMap.get(day) != null) {

                BusinessHour businessHour = businessHourMap.get(day);
                SingleBusinessHourRequest singleBusinessHourRequest = requestMap.get(day);
                businessHour.updateBusinessHour(singleBusinessHourRequest);
            } else if (businessHourMap.get(day) == null && requestMap.get(day) != null) {
                // 기존 영업시간은 없는데 새로운 영업시간이 추가되는 경우 새로운 영업시간 추가

                SingleBusinessHourRequest singleBusinessHourRequest = requestMap.get(day);
                BusinessHour businessHour = BusinessHour.ofCreateBusinessHour(singleBusinessHourRequest, storeMeta);
                businessHourRepository.save(businessHour);
            } else if (businessHourMap.get(day) != null && requestMap.get(day) == null) {
                // 기존 영업시간은 있는데, 업데이트 영업시간에는 없는 경우 기존 영업시간 삭제

                BusinessHour businessHour = businessHourMap.get(day);
                businessHourRepository.deleteById(businessHour.getId());
            }
        }
    }
}
