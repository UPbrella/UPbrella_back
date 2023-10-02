package upbrella.be.user.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import upbrella.be.user.dto.request.KakaoAccount;

import java.io.Serializable;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.*;

@Getter
@Builder
@JsonNaming(SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class KakaoLoginResponse implements Serializable {

    private Long id;
    private KakaoAccount kakaoAccount;
}
