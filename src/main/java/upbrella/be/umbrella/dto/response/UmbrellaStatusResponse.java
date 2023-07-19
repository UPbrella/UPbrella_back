package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UmbrellaStatusResponse {

    private int id;
    private int uuid;
    private String statusContent;
    private String etc;
}