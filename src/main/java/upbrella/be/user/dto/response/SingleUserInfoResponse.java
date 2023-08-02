package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.user.entity.User;

@Getter
@Builder
public class SingleUserInfoResponse {

    private String name;
    private String phoneNumber;
    private String bank;
    private String accountNumber;

    public static SingleUserInfoResponse fromUser(User user) {

        return SingleUserInfoResponse.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .bank(user.getBank())
                .accountNumber(user.getAccountNumber())
                .build();
    }
}
