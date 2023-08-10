package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UmbrellaAllStatisticsResponse {

    private int totalUmbrellaCount;
    private int rentableUmbrellaCount;
    private int rentedUmbrellaCount;
    private int missingUmbrellaCount;
    private double missingRate;

    public static UmbrellaAllStatisticsResponse fromCounts(int totalUmbrellaCount, int rentableUmbrellaCount, int rentedUmbrellaCount, int missingUmbrellaCount) {

        return UmbrellaAllStatisticsResponse.builder()
                .totalUmbrellaCount(totalUmbrellaCount)
                .rentableUmbrellaCount(rentableUmbrellaCount)
                .rentedUmbrellaCount(rentedUmbrellaCount)
                .missingUmbrellaCount(missingUmbrellaCount)
                .missingRate((double) 100*missingUmbrellaCount / totalUmbrellaCount)
                .build();
    }
}
