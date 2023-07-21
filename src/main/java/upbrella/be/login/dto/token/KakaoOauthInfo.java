package upbrella.be.login.dto.token;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoOauthInfo implements CommonOauthInfo {

    @Value("${KAKAO_CLIENT_ID_DEV}")
    private String clientId;
    @Value("${KAKAO_CLIENT_SECRET_DEV}")
    private String clientSecret;
    @Value("${KAKAO_REDIRECT_URI_DEV}")
    private String redirectUri;
    @Value("${KAKAO_LOGIN_URI_DEV}")
    private String loginUri;
}
