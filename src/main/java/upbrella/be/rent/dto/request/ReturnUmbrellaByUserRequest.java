package upbrella.be.rent.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnUmbrellaByUserRequest {

    private int umbrellaId;
    private int storeId;
    private String improvement;
}
