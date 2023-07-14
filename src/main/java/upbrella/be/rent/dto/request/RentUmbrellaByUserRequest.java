package upbrella.be.rent.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RentUmbrellaByUserRequest {

    private String name;
    private String phoneNumber;
    private String region;
    private int storeId;
    private int umbrellaId;
    private String statusDeclaration;
}
