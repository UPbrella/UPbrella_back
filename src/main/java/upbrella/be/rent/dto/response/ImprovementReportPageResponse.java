package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ImprovementReportPageResponse {

    private List<ImprovementReportResponse> improvementReports;

    public static ImprovementReportPageResponse of(List<ImprovementReportResponse> improvementReports) {

        return ImprovementReportPageResponse.builder()
                .improvementReports(improvementReports)
                .build();
    }
}
