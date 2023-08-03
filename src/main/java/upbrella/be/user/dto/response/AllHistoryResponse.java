package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllHistoryResponse {

    private List<SingleHistoryResponse> histories;
}
