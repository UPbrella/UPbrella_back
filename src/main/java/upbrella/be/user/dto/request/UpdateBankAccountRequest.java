package upbrella.be.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class UpdateBankAccountRequest {

    @Range(min = 1, max = 10)
    @NotBlank
    private String bank;
    @Range(min = 1, max = 45)
    @NotBlank
    private String accountNumber;
}
