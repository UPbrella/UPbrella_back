package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RentalHistoriesPageResponse {

    private List<RentalHistoryResponse> rentalHistoryResponsePage;

    public static RentalHistoriesPageResponse of(List<RentalHistoryResponse> rentalHistories) {

        return RentalHistoriesPageResponse.builder()
                .rentalHistoryResponsePage(rentalHistories)
                .build();
    }
}
