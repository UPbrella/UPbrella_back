package upbrella.be.rent.dto.response;

import lombok.*;
import upbrella.be.rent.entity.ConditionReport;

@Getter
@Builder
public class ConditionReportResponse {

    private long id;
    private long umbrellaUuid;
    private String content;
    private String etc;

    public static ConditionReportResponse fromConditionReport(ConditionReport conditionReport) {

        return ConditionReportResponse.builder()
                .id(conditionReport.getHistory().getId())
                .umbrellaUuid(conditionReport.getHistory().getUmbrella().getUuid())
                .content(conditionReport.getContent())
                .etc(conditionReport.getEtc())
                .build();
    }
}
