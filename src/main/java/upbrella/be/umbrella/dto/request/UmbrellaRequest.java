package upbrella.be.umbrella.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UmbrellaRequest {

    private long storeMetaId;
    private long uuid;
    private boolean rentable;
}