package upbrella.be.rent.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReturnUmbrellaByUserRequest {

    private String bank;
    private String accountNumber;
    private String improvementReportContent;
}
