package upbrella.be.login.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaverLoginCodeRequest {

    private String code;
    private String state;
}
