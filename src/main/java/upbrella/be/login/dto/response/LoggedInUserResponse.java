package upbrella.be.login.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoggedInUserResponse {

    private long socialId;
}
