package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UmbrellaPageResponse {

    private List<UmbrellaResponse> umbrellaResponsePage;
}