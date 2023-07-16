package upbrella.be.umbrella.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UmbrellaRequest {

    private int storeMetaId;
    private int umbrellaId;
    private boolean rentable;
}