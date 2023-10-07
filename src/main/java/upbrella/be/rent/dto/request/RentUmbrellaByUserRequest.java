package upbrella.be.rent.dto.request;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RentUmbrellaByUserRequest {

    private String region;
    private long storeId;
    private long umbrellaId;

    @Size(max = 400, message = "conditionReport는 최대 400자여야 합니다.")
    private String conditionReport;
}
