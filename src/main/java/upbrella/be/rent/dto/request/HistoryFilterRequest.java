package upbrella.be.rent.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HistoryFilterRequest {

    private Boolean refunded;
}
