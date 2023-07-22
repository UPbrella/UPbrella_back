package upbrella.be.rent.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentUmbrellaByUserRequest {

    private String region;
    private long storeId;
    private long umbrellaId;
    private String conditonReport;
}
