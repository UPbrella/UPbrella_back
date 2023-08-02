package upbrella.be.rent.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReturnUmbrellaByUserRequest {

    private long uuid;
    private long storeId;
    private String improvement;
}
