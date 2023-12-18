package upbrella.be.rent.entity;

import lombok.*;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.exception.NotRefundedException;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.dto.response.SingleHistoryResponse;
import upbrella.be.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "umbrella_id")
    private Umbrella umbrella;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime paidAt;
    private String bank;
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rent_store_meta_id")
    private StoreMeta rentStoreMeta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_store_meta_id")
    private StoreMeta returnStoreMeta;

    private LocalDateTime rentedAt;
    private LocalDateTime returnedAt;
    private LocalDateTime refundedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refunded_by")
    private User refundedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by")
    private User paidBy;
    private String etc;

    public void refund(User user, LocalDateTime refundedAt) {

        if (this.refundedAt != null || this.refundedBy != null) {
            this.refundedAt = null;
            this.refundedBy = null;
            return;
        }

        this.refundedAt = refundedAt;
        this.refundedBy = user;
    }

    public void paid(User user, LocalDateTime paidAt) {

        if (this.paidAt != null || this.paidBy != null) {
            this.paidAt = null;
            this.paidBy = null;
            return;
        }
        this.paidBy = user;
        this.paidAt = paidAt;
    }

    public static History ofCreatedByNewRent(Umbrella umbrella, User user, StoreMeta rentStoreMeta) {

        return History.builder()
                .umbrella(umbrella)
                .user(user)
                .rentStoreMeta(rentStoreMeta)
                .rentedAt(LocalDateTime.now())
                .build();
    }

    public static History updateHistoryForReturn(History rentedHistory, StoreMeta returnStoreMeta, ReturnUmbrellaByUserRequest request) {

        return History.builder()
                .id(rentedHistory.getId())
                .umbrella(rentedHistory.getUmbrella())
                .user(rentedHistory.getUser())
                .paidAt(rentedHistory.getPaidAt())
                .bank(request.getBank())
                .accountNumber(request.getAccountNumber())
                .rentStoreMeta(rentedHistory.getRentStoreMeta())
                .returnStoreMeta(returnStoreMeta)
                .rentedAt(rentedHistory.getRentedAt())
                .returnedAt(LocalDateTime.now())
                .build();
    }

    public static SingleHistoryResponse ofUserHistory(History history) {

        boolean isReturned = true;
        boolean isRefunded = false;

        LocalDateTime returnAt = history.getReturnedAt();

        if (returnAt == null) {
            isReturned = false;
            returnAt = history.getRentedAt().plusDays(7);
        }

        if (history.getRefundedAt() != null) {
            isRefunded = true;
        }

        return SingleHistoryResponse.builder()
                .umbrellaUuid(history.getUmbrella().getUuid())
                .rentedAt(history.getRentedAt())
                .rentedStore(history.getRentStoreMeta().getName())
                .returnAt(returnAt)
                .isReturned(isReturned)
                .isRefunded(isRefunded)
                .build();
    }

    public void deleteBankAccount() {

        if (this.refundedAt == null) {
            throw new NotRefundedException("[ERROR] 보증금 환급이 완료되지 않았습니다.");
        }
        this.bank = null;
        this.accountNumber = null;
    }
}
