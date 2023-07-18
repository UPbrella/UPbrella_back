package upbrella.be.rent.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalHistoryResponse {

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
