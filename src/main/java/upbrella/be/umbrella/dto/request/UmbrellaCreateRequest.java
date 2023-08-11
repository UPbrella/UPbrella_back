package upbrella.be.umbrella.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UmbrellaCreateRequest {

    private long storeMetaId;
    private long uuid;
    private boolean rentable;
}
