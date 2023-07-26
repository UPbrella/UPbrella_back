package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import upbrella.be.user.entity.User;

@Getter
@Setter
@Builder
public class UserInfoResponse {

    private long id;
    private String name;
    private String phoneNumber;

    public static UserInfoResponse fromUser(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
