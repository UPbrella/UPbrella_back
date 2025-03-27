package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import upbrella.be.user.entity.User;

@Getter
@Builder
public class SessionUser implements Serializable {

    private long id;
    private long socialId;
    private Boolean adminStatus;

    public static SessionUser fromUser(User user) {

        return SessionUser.builder()
                .id(user.getId())
                .adminStatus(user.getAdminStatus())
                .build();
    }
}
