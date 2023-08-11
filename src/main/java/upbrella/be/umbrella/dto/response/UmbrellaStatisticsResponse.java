package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UmbrellaStatisticsResponse {

    private long totalUmbrellaCount;
    private long rentableUmbrellaCount;
    private long rentedUmbrellaCount;
    private long missingUmbrellaCount;
    private double missingRate;
    private long totalRentCount;

    public static UmbrellaStatisticsResponse fromCounts(long totalUmbrellaCount, long rentableUmbrellaCount,
                                                        long rentedUmbrellaCount, long missingUmbrellaCount,
                                                        long totalRentCount) {

        return UmbrellaStatisticsResponse.builder()
                .totalUmbrellaCount(totalUmbrellaCount)
                .rentableUmbrellaCount(rentableUmbrellaCount)
                .rentedUmbrellaCount(rentedUmbrellaCount)
                .missingUmbrellaCount(missingUmbrellaCount)
                .missingRate((double) 100 * missingUmbrellaCount / totalUmbrellaCount)
                .totalRentCount(totalRentCount)
                .build();
    }
}
