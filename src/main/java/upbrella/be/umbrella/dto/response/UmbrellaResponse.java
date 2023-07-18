package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UmbrellaResponse {

    private int id;
    private int storeMetaId;
    private int uuid;
    private boolean rentable;
}