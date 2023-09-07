package upbrella.be.user.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JoinRequest {

    private String name;
    private String phoneNumber;
    private String email;
    private String bank;
    private String accountNumber;
}
