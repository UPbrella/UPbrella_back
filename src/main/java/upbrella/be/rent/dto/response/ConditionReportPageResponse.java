package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ConditionReportPageResponse {

    private List<ConditionReportResponse> conditionReports;
}
