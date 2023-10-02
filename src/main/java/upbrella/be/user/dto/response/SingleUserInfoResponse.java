package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.user.entity.User;

@Getter
@Builder
public class SingleUserInfoResponse {

    private long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String bank;
    private String accountNumber;
    private boolean adminStatus;

    public static SingleUserInfoResponse fromUser(User user) {

        return SingleUserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .bank(user.getBank())
                .accountNumber(user.getAccountNumber())
                .adminStatus(user.isAdminStatus())
                .build();
    }
}
