package upbrella.be.rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.rent.entity.ConditionReport;
import upbrella.be.rent.repository.ConditionReportRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionReportService {

    private final ConditionReportRepository conditionReportRepository;

    public List<ConditionReport> findAllConditionReport() {
        return conditionReportRepository.findAllConditionReport();
    }
}
