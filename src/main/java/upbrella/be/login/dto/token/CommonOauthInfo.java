package upbrella.be.login.dto.token;

public interface CommonOauthInfo {

    String getClientId();
    String getClientSecret();
    String getRedirectUri();
    String getLoginUri();
}
