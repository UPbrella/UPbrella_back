package upbrella.be.rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.rent.dto.response.ImprovementReportPageResponse;
import upbrella.be.rent.dto.response.ImprovementReportResponse;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.entity.ImprovementReport;
import upbrella.be.rent.repository.ImprovementReportRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImprovementReportService {

    private final ImprovementReportRepository improvementReportRepository;

    public ImprovementReportPageResponse findAll() {

        return ImprovementReportPageResponse.of(findAllImprovementReport());
    }

    public void addImprovementReportFromReturn(History history, String content) {

        ImprovementReport fromReturn = ImprovementReport.createFromReturn(history, content);
        improvementReportRepository.save(fromReturn);
    }

    private List<ImprovementReportResponse> findAllImprovementReport() {

        return improvementReportRepository.findAll()
                .stream()
                .map(ImprovementReportResponse::fromImprovementReport)
                .collect(Collectors.toList());
    }
}
