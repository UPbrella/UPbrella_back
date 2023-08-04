package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.user.entity.User;

@Getter
@Builder
public class SingleUserInfoResponse {

    private long id;
    private long socialId;
    private String name;
    private String phoneNumber;
    private String bank;
    private String accountNumber;
    private boolean adminStatus;

    public static SingleUserInfoResponse fromUser(User user) {

        return SingleUserInfoResponse.builder()
                .id(user.getId())
                .socialId(user.getSocialId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .bank(user.getBank())
                .accountNumber(user.getAccountNumber())
                .adminStatus(user.isAdminStatus())
                .build();
    }
}
