package upbrella.be.rent.entity;

import lombok.*;
import upbrella.be.payment.entity.Payment;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class History {

    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "umbrella_id")
    private Umbrella umbrella;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "rent_store_meta_id")
    private StoreMeta rentStoreMetaId;
    @ManyToOne
    @JoinColumn(name = "return_store_meta_id")
    private StoreMeta returnStoreMetaId;
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
    private LocalDateTime rentedAt;
    private LocalDateTime returnedAt;
    private String etc;

    public static History ofCreatedByNewRent(Umbrella umbrella, User user, StoreMeta rentStoreMetaId) {
        return History.builder()
                .umbrella(umbrella)
                .user(user)
                .rentStoreMetaId(rentStoreMetaId)
                .rentedAt(LocalDateTime.now())
                .build();
    }
}
