package upbrella.be.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleClassificationResponse {

    private long id;
    private String type;
    private String name;
    private double latitude;
    private double longitude;

    @QueryProjection
    public SingleClassificationResponse(long id, String type, String name, double latitude, double longitude) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
