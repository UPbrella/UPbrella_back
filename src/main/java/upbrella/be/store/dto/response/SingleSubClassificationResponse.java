package upbrella.be.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleSubClassificationResponse {

    private long id;
    private String type;
    private String name;

    @QueryProjection
    public SingleSubClassificationResponse(long id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
}
