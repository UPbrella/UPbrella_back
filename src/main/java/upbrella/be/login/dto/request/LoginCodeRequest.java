package upbrella.be.login.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginCodeRequest {

    private String code;
}
