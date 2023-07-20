package upbrella.be.login.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import upbrella.be.user.dto.response.UserInfoResponse;
import upbrella.be.user.entity.User;

@Getter
@Setter
@Builder
public class LoggedInUserResponse {

    private long id;
    private String name;
    private String phoneNumber;
    private boolean adminStatus;

    public static LoggedInUserResponse loggedInUser(User user) {

        return LoggedInUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .adminStatus(user.isAdminStatus())
                .build();
    }
}
