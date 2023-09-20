package upbrella.be.rent.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import upbrella.be.rent.dto.HistoryInfoDto;
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
    private Integer totalRentalDay;
    private boolean refundCompleted;
    private boolean paid;
    private String bank;
    private String accountNumber;
    private String etc;

    private static boolean isRefunded(History history) {

        return history.getRefundedAt() != null;
    }

    public static RentalHistoryResponse createReturnedHistory(HistoryInfoDto history, int elapsedDay, int totalRentalDay) {

        return RentalHistoryResponse.builder()
                .id(history.getId())
                .name(history.getName())
                .phoneNumber(history.getPhoneNumber())
                .rentStoreName(history.getRentStoreName())
                .rentAt(history.getRentAt())
                .elapsedDay(elapsedDay)
                .paid(history.getPaidAt() != null)
                .umbrellaUuid(history.getUmbrellaUuid())
                .returnStoreName(history.getReturnStoreName())
                .returnAt(history.getReturnAt())
                .totalRentalDay(totalRentalDay)
                .refundCompleted(history.getRefundedAt() != null)
                .bank(history.getBank())
                .accountNumber(history.getAccountNumber())
                .etc(history.getEtc())
                .build();
    }

    public static RentalHistoryResponse createNonReturnedHistory(HistoryInfoDto history, int elapsedDay) {

        return RentalHistoryResponse.builder()
                .id(history.getId())
                .name(history.getName())
                .phoneNumber(history.getPhoneNumber())
                .rentStoreName(history.getRentStoreName())
                .rentAt(history.getRentAt())
                .elapsedDay(elapsedDay)
                .paid(history.getPaidAt() != null)
                .umbrellaUuid(history.getUmbrellaUuid())
                .refundCompleted(history.getRefundedAt() != null)
                .bank(history.getBank())
                .accountNumber(history.getAccountNumber())
                .etc(history.getEtc())
                .build();
    }
}
