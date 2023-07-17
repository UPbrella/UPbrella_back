package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserInfoResponse {

    private int id;
    private long socialId;
    private String name;
    private String phoneNumber;
    private boolean adminStatus;
}
