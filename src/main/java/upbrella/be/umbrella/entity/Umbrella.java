package upbrella.be.umbrella.entity;

import lombok.*;
import upbrella.be.store.entity.StoreMeta;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Umbrella {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "store_meta_id")
    private StoreMeta storeMeta;
    private long uuid;
    private boolean rentable;
    private boolean deleted;

    public static Umbrella ofCreated(StoreMeta storeMeta, long uuid, boolean rentable) {
        return Umbrella.builder()
                .storeMeta(storeMeta)
                .uuid(uuid)
                .rentable(rentable)
                .deleted(false)
                .build();
    }
}
