package upbrella.be.login.dto.token;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class NaverOauthInfo implements CommonOauthInfo {

    @Value("${NAVER_CLIENT_ID_DEV}")
    private String clientId;
    @Value("${NAVER_CLIENT_SECRET_DEV}")
    private String clientSecret;
    @Value("${NAVER_REDIRECT_URI_DEV}")
    private String redirectUri;
    @Value("${NAVER_LOGIN_URI_DEV}")
    private String loginUri;
}
