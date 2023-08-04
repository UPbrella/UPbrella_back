package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AllUsersInfoResponse {

    private List<SingleUserInfoResponse> users;

    public static AllUsersInfoResponse fromUsers(List<User> users) {

        return AllUsersInfoResponse.builder()
                .users(
                        users.stream()
                                .map(SingleUserInfoResponse::fromUser)
                                .collect(Collectors.toList())
                ).build();
    }
}
