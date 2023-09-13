package upbrella.be.umbrella.dto.request;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UmbrellaCreateRequest {

    @Min(1)
    @Max(9_223_372_036_854_775_807L)
    private long storeMetaId;
    @Min(1)
    @Max(9_223_372_036_854_775_807L)
    private long uuid;
    private boolean rentable;
    @Size(max = 100)
    private String etc;
}
