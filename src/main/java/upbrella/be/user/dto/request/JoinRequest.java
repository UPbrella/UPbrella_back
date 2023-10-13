package upbrella.be.user.dto.request;

import lombok.*;
import upbrella.be.user.validation.OnlyNumbers;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JoinRequest {

    @NotBlank
    @Size(max = 6)
    private String name;
    @NotBlank
    @Size(max = 16)
    @Pattern(regexp = "^\\d{3}-?\\d{4}-?\\d{4}$", message = "유효한 전화번호 형식이 아닙니다.")
    private String phoneNumber;
    @Size(max = 40)
    private String bank;
    @Size(max = 40)
    @OnlyNumbers
    private String accountNumber;
}
