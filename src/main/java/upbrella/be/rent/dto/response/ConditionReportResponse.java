package upbrella.be.rent.dto.response;

import lombok.*;
import upbrella.be.rent.entity.ConditionReport;

@Getter
@Builder
public class ConditionReportResponse {

    private long id;
    private long umbrellaId;
    private String content;
    private String etc;

    public static ConditionReportResponse fromConditionReport(ConditionReport conditionReport) {

        return ConditionReportResponse.builder()
                .id(conditionReport.getId())
                .umbrellaId(conditionReport.getHistory().getId())
                .content(conditionReport.getContent())
                .etc(conditionReport.getEtc())
                .build();
    }
}
