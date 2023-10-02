package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.user.entity.User;

import java.io.Serializable;

@Getter
@Builder
public class SessionUser implements Serializable {

    private long id;
    private long socialId;
    private Boolean adminStatus;

    public static SessionUser fromUser(User user) {

        return SessionUser.builder()
                .id(user.getId())
                .adminStatus(user.isAdminStatus())
                .build();
    }
}
