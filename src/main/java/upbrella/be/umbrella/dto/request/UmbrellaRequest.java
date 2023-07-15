package upbrella.be.umbrella.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UmbrellaRequest {

    private int storeMetaId;
    private int umbrellaId;
    private boolean rentable;
}