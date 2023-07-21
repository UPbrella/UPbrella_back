package upbrella.be.rent.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionReportResponse {

    private long id;
    private long umbrellaId;
    private String content;
    private String etc;
}
