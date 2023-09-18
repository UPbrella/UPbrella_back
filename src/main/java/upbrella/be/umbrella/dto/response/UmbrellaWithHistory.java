package upbrella.be.umbrella.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.StoreMeta;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class UmbrellaWithHistory {

    private long id;
    private StoreMeta storeMeta;
    private long uuid;
    private boolean rentable;
    private boolean deleted;
    private LocalDateTime createdAt;
    private String etc;
    private boolean missed;
    private Long historyId;

    @QueryProjection
    public UmbrellaWithHistory(long id, StoreMeta storeMeta, long uuid, boolean rentable, boolean deleted, LocalDateTime createdAt, String etc, boolean missed, Long historyId) {

        this.id = id;
        this.storeMeta = storeMeta;
        this.uuid = uuid;
        this.rentable = rentable;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.etc = etc;
        this.missed = missed;
        this.historyId = historyId;
    }
}
