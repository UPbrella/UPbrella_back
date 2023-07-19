package upbrella.be.rent.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime rentAt;
    private int elapsedDay; // 경과 시간
    private int umbrellaId;
    private String returnStoreName;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime returnAt;
    private int totalRentalDay;
    private boolean refundCompleted;
    private String etc;
}
