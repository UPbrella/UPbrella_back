package upbrella.be.store.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class BusinessHour {

    @Id
    @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreMeta storeMeta;
    @Enumerated(EnumType.STRING)
    private DayOfWeek date;
    private LocalTime openAt;
    private LocalTime closeAt;
}
