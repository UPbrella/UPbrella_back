package upbrella.be.user.dto.response;

import lombok.*;

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
