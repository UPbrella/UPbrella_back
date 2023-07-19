package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UmbrellaResponse {

    private long id;
    private long storeMetaId;
    private long uuid;
    private boolean rentable;
}