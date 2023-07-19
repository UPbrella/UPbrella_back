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
    private String coordinate; // 네이버 길찾기 누르면 바로 좌표 길찾기로 이용할 수 있도록
}
