package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ConditionReportPageResponse {

    private List<ConditionReportResponse> conditionReports;

    public static ConditionReportPageResponse of(List<ConditionReportResponse> conditionReports) {

        return ConditionReportPageResponse.builder()
                .conditionReports(conditionReports)
                .build();
    }
}
