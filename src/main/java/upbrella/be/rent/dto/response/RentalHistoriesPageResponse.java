package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RentalHistoriesPageResponse {

    private List<RentalHistoryResponse> rentalHistoryResponsePage;
    private long countOfAllHistories;
    private long countOfAllPages;

    public static RentalHistoriesPageResponse of(List<RentalHistoryResponse> rentalHistories, long countOfAllHistories, long countOfAllPages) {

        return RentalHistoriesPageResponse.builder()
                .rentalHistoryResponsePage(rentalHistories)
                .countOfAllHistories(countOfAllHistories)
                .countOfAllPages(countOfAllPages)
                .build();
    }
}
