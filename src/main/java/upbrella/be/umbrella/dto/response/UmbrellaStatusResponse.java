package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UmbrellaStatusResponse {

    private long id;
    private long uuid;
    private String statusContent;
    private String etc;
}