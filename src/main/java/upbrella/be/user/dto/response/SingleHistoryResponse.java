package upbrella.be.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SingleHistoryResponse {

    private long umbrellaUuid;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime rentedAt;
    private String rentedStore;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime returnAt;
    private boolean isReturned;
    private boolean isRefunded;
}
