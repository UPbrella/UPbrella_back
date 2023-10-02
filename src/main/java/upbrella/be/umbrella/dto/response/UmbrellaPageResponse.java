package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UmbrellaPageResponse {

    private List<UmbrellaResponse> umbrellaResponsePage;
}