package upbrella.be.rent.dto.request;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReturnUmbrellaByUserRequest {

    private long returnStoreId;
    private String bank;
    private String accountNumber;

    @Size(max = 400, message = "최대 400자여야 합니다.")
    private String improvementReportContent;
}
