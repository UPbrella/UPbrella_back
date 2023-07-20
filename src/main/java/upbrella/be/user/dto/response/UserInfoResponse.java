package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserInfoResponse {

    private long id;
    private String name;
    private String phoneNumber;
    private boolean adminStatus;
}
