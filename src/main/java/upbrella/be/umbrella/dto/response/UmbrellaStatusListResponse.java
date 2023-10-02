package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class UmbrellaStatusListResponse {

    private Page<UmbrellaStatusResponse> umbrellaStatusResponsePage;
}