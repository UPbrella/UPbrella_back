package upbrella.be.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLockerRequest {

    private long storeId;
    @Size(min = 32)
    private String secretKey;
}
