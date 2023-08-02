package upbrella.be.rent.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImprovementReportResponse {

    private long id;
    private long umbrellaId;
    private String content;
    private String etc;
}
