package upbrella.be.login.dto.token;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class NaverOauthInfo {

    @Value("${NAVER_CLIENT_ID_DEV}")
    private String naverClientId;
    @Value("${NAVER_CLIENT_SECRET_DEV}")
    private String naverClientSecret;
    @Value("${NAVER_STATE_DEV}")
    private String naverState;
    @Value("${NAVER_REDIRECT_URI_DEV}")
    private String naverUrl;
}
