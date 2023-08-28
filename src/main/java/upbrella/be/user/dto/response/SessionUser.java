package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.user.entity.User;

@Getter
@Builder
public class SessionUser {

    private long id;
    private long socialId;
    private String name;
    private String phoneNumber;
    private boolean adminStatus;

    public static SessionUser fromUser(User user) {

        return SessionUser.builder()
                .id(user.getId())
                .socialId(user.getSocialId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .adminStatus(user.isAdminStatus())
                .build();
    }
}
