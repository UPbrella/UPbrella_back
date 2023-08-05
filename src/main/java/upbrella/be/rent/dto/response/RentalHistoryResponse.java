package upbrella.be.rent.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import upbrella.be.rent.entity.History;

import java.time.LocalDateTime;

@Getter
@Builder
public class RentalHistoryResponse {

    private long id;
    private String name;
    private String phoneNumber;
    private String rentStoreName;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime rentAt;
    private int elapsedDay; // 경과 시간
    private long umbrellaUuid;
    private String returnStoreName;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime returnAt;
    private int totalRentalDay;
    private boolean refundCompleted;
    private String etc;

    public static RentalHistoryResponse createReturnedHistory(History history, int elapsedDay, int totalRentalDay, boolean isReturned) {

        return RentalHistoryResponse.builder()
                .id(history.getId())
                .name(history.getUser().getName())
                .phoneNumber(history.getUser().getPhoneNumber())
                .rentStoreName(history.getRentStoreMeta().getName())
                .rentAt(history.getRentedAt())
                .elapsedDay(elapsedDay)
                .umbrellaUuid(history.getUmbrella().getUuid())
                .returnStoreName(history.getReturnStoreMeta().getName())
                .returnAt(history.getReturnedAt())
                .totalRentalDay(totalRentalDay)
                .refundCompleted(isReturned)
                .etc(history.getEtc())
                .build();
    }

    public static RentalHistoryResponse createNonReturnedHistory(History history, int elapsedDay, int totalRentalDay, boolean isReturned) {

        return RentalHistoryResponse.builder()
                .id(history.getId())
                .name(history.getUser().getName())
                .phoneNumber(history.getUser().getPhoneNumber())
                .rentStoreName(history.getRentStoreMeta().getName())
                .rentAt(history.getRentedAt())
                .elapsedDay(elapsedDay)
                .umbrellaUuid(history.getUmbrella().getUuid())
                .totalRentalDay(totalRentalDay)
                .refundCompleted(isReturned)
                .etc(history.getEtc())
                .build();
    }
}
