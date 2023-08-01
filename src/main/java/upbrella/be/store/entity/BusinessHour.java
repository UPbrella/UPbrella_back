package upbrella.be.store.entity;

import lombok.*;
import upbrella.be.store.dto.request.SingleBusinessHourRequest;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class BusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_meta_id")
    private StoreMeta storeMeta;
    @Enumerated(EnumType.STRING)
    private DayOfWeek date;
    private LocalTime openAt;
    private LocalTime closeAt;

    public static BusinessHour ofCreateBusinessHour(SingleBusinessHourRequest businessHourResponse, StoreMeta storeMeta) {

        return BusinessHour.builder()
                .storeMeta(storeMeta)
                .date(businessHourResponse.getDate())
                .openAt(businessHourResponse.getOpenAt())
                .closeAt(businessHourResponse.getCloseAt())
                .build();
    }

    public void updateBusinessHour(SingleBusinessHourRequest businessHour) {

        this.date = businessHour.getDate();
        this.openAt = businessHour.getOpenAt();
        this.closeAt = businessHour.getCloseAt();
    }
}
