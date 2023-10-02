package upbrella.be.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import upbrella.be.user.entity.BlackList;

import java.time.LocalDateTime;

@Getter
@Builder
public class SingleBlackListResponse {

    private long id;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime blockedAt;

    public static SingleBlackListResponse of(BlackList blackList) {

        return SingleBlackListResponse.builder()
                .id(blackList.getId())
                .blockedAt(blackList.getBlockedAt())
                .build();
    }
}
