package upbrella.be.umbrella.entity;

import lombok.*;
import upbrella.be.store.entity.StoreMeta;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Umbrella {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "store_meta_id")
    private StoreMeta storeMeta;
    private long uuid;
    private boolean rentable;
    private boolean deleted;

    public void delete() {
        this.deleted = true;
    }

    public static Umbrella ofCreated(StoreMeta storeMeta, long uuid, boolean rentable) {
        return Umbrella.builder()
                .storeMeta(storeMeta)
                .uuid(uuid)
                .rentable(rentable)
                .deleted(false)
                .build();
    }

    public static Umbrella ofUpdated(long id, StoreMeta storeMeta, long uuid, boolean rentable) {
        return Umbrella.builder()
                .id(id)
                .storeMeta(storeMeta)
                .uuid(uuid)
                .rentable(rentable)
                .deleted(false)
                .build();
    }
}
