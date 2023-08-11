package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UmbrellaStatisticsResponse {

    private int totalUmbrellaCount;
    private int rentableUmbrellaCount;
    private int rentedUmbrellaCount;
    private int missingUmbrellaCount;
    private double missingRate;
    private long totalRentCount;

    public static UmbrellaStatisticsResponse fromCounts(int totalUmbrellaCount, int rentableUmbrellaCount,
                                                        int rentedUmbrellaCount, int missingUmbrellaCount,
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
