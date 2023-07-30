package upbrella.be.umbrella.entity;

import lombok.*;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_meta_id")
    private StoreMeta storeMeta;
    private long uuid;
    private boolean rentable;
    private boolean deleted;

    public void delete() {
        this.deleted = true;
    }

    public static Umbrella ofCreated(UmbrellaRequest umbrellaRequest, StoreMeta storeMeta) {

        return Umbrella.builder()
                .storeMeta(storeMeta)
                .uuid(umbrellaRequest.getUuid())
                .rentable(umbrellaRequest.isRentable())
                .deleted(false)
                .build();
    }

    public static Umbrella ofUpdated(long id, UmbrellaRequest umbrellaRequest, StoreMeta storeMeta) {

        return Umbrella.builder()
                .id(id)
                .storeMeta(storeMeta)
                .uuid(umbrellaRequest.getUuid())
                .rentable(umbrellaRequest.isRentable())
                .deleted(false)
                .build();
    }
}
