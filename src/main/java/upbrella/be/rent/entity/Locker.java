package upbrella.be.rent.entity;

import lombok.*;
import upbrella.be.store.entity.StoreMeta;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Locker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "store_meta_id")
    private StoreMeta storeMeta;
    private long count;
    private String secretKey;
    private LocalDateTime lastAccess;

    public void updateCount() {
        this.count += 1;
    }

    public void updateLastAccess(LocalDateTime now) {
        this.lastAccess = now;
    }
}
