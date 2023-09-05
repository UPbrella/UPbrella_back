package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.user.entity.BlackList;

import java.time.LocalDateTime;

@Getter
@Builder
public class SingleBlackListResponse {

    private long id;
    private long socialId;
    private LocalDateTime blockedAt;

    public static SingleBlackListResponse of(BlackList blackList) {

        return SingleBlackListResponse.builder()
                .id(blackList.getId())
                .socialId(blackList.getSocialId())
                .blockedAt(blackList.getBlockedAt())
                .build();
    }
}
