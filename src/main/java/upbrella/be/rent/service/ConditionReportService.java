package upbrella.be.rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.rent.dto.response.ConditionReportResponse;
import upbrella.be.rent.repository.ConditionReportRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConditionReportService {

    private final ConditionReportRepository conditionReportRepository;

    public List<ConditionReportResponse> findAllConditionReport() {

        return conditionReportRepository.findAll()
                .stream()
                .map(ConditionReportResponse::fromConditionReport)
                .collect(Collectors.toList());
    }
}
