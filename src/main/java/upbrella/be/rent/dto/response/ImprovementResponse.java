package upbrella.be.rent.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImprovementResponse {

    private long id;
    private int umbrellaId;
    private String content;
    private String etc;
}
