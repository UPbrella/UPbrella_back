package upbrella.be.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import upbrella.be.rent.entity.History;

import java.time.LocalDateTime;

@Getter
@Builder
public class SingleHistoryResponse {

    private long umbrellaUuid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime rentedAt;
    private String rentedStore;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime returnAt;
    private boolean isReturned;
    private boolean isRefunded;

    public static SingleHistoryResponse ofUserHistory(History history, LocalDateTime returnAt, boolean isReturned, boolean isRefunded) {

        return SingleHistoryResponse.builder()
                .umbrellaUuid(history.getUmbrella().getUuid())
                .rentedAt(history.getRentedAt())
                .rentedStore(history.getRentStoreMeta().getName())
                .returnAt(returnAt)
                .isReturned(isReturned)
                .isRefunded(isRefunded)
                .build();
    }
}
