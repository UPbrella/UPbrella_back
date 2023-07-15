package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
public class UmbrellaStatusListResponse {

    private Page<UmbrellaStatusResponse> umbrellaStatusResponsePage;
}