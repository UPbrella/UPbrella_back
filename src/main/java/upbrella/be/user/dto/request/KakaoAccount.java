package upbrella.be.user.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;


@Getter
@Builder
public class KakaoAccount implements Serializable {

    private String email;
}
