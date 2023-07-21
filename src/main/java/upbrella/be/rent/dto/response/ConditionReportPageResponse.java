package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ConditionReportPageResponse {

    private List<ConditionReportResponse> conditionReportResponses;
}
