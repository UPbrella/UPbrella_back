package upbrella.be.login.dto.token;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoOauthInfo {

    @Value("${KAKAO_CLIENT_ID_DEV}")
    private String kakoClientId;
    @Value("${KAKAO_CLIENT_SECRET_DEV}")
    private String kakaoClientSecret;
    @Value("${KAKAO_REDIRECT_URI_DEV}")
    private String kakaorUrl;
}
