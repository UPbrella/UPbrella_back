package upbrella.be.rent.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusDeclarationResponse {

    private long id;
    private long umbrellaId;
    private String content;
    private String etc;
}
