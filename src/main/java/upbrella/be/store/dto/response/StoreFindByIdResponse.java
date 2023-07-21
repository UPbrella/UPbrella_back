package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StoreFindByIdResponse {

    private long id;
    private String name;
    private String businessHours;
    private String contactNumber;
    private String address;
    private int availableUmbrellaCount;
    private boolean openStatus;
    private String latitude;
    private String longitude;
}
