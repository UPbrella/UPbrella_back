package upbrella.be.rent.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistoryInfoDto {

    private long id;
    private String name;
    private String phoneNumber;
    private String rentStoreName;
    private LocalDateTime rentAt;
    private long umbrellaUuid;
    private String returnStoreName;
    private LocalDateTime returnAt;
    private LocalDateTime paidAt;
    private String bank;
    private String accountNumber;
    private String etc;
    private LocalDateTime refundedAt;

    @QueryProjection
    public HistoryInfoDto(long id, String name, String phoneNumber, String rentStoreName, LocalDateTime rentAt, long umbrellaUuid, String returnStoreName, LocalDateTime returnAt, LocalDateTime paidAt, String bank, String accountNumber, String etc, LocalDateTime refundedAt) {

        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.rentStoreName = rentStoreName;
        this.rentAt = rentAt;
        this.umbrellaUuid = umbrellaUuid;
        this.returnStoreName = returnStoreName;
        this.returnAt = returnAt;
        this.paidAt = paidAt;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.etc = etc;
        this.refundedAt = refundedAt;
    }
}
