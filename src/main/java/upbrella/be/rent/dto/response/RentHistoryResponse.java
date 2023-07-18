package upbrella.be.rent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentHistoryResponse {

    private long id;
    private String name;
    private String phoneNumber;
    private String rentStoreName;
    private LocalDateTime rentAt;
    private int elapsedDay; // 경과 시간
    private int umbrellaId;
    private String returnStoreName;
    private LocalDateTime returnAt;
    private int totalRentalDay;
    private boolean refundCompleted;
}
