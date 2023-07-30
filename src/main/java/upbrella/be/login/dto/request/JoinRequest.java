package upbrella.be.login.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JoinRequest {

    private String name;
    private String phoneNumber;
    private String bank;
    private String accountNumber;
}
