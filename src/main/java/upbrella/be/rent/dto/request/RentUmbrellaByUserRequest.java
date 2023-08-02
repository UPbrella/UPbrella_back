package upbrella.be.rent.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RentUmbrellaByUserRequest {

    private String region;
    private long storeId;
    private long umbrellaId;
    private String conditionReport;
}
