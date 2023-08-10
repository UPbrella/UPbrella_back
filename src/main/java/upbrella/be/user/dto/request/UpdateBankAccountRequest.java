package upbrella.be.user.dto.request;

import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateBankAccountRequest {

    @Size(min = 1, max = 10)
    @NotBlank
    private String bank;
    @Size(min = 1, max = 45)
    @NotBlank
    private String accountNumber;
}
