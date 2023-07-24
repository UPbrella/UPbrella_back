package upbrella.be.rent.dto.response;

import lombok.*;
import upbrella.be.rent.entity.ConditionReport;

@Getter
@Setter
@Builder
public class ConditionReportResponse {

    private long id;
    private long umbrellaId;
    private String content;
    private String etc;

    public static ConditionReportResponse from(ConditionReport conditionReport) {
        return ConditionReportResponse.builder()
                .id(conditionReport.getId())
                .umbrellaId(conditionReport.getHistory().getId())
                .content(conditionReport.getContent())
                .etc(conditionReport.getEtc())
                .build();
    }
}