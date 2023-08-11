package upbrella.be.umbrella.entity;

import lombok.*;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest;
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    private boolean missed;

    public void delete() {
        this.deleted = true;
    }

    public static Umbrella ofCreated(UmbrellaCreateRequest umbrellaCreateRequest, StoreMeta storeMeta) {

        return Umbrella.builder()
                .storeMeta(storeMeta)
                .uuid(umbrellaCreateRequest.getUuid())
                .rentable(umbrellaCreateRequest.isRentable())
                .deleted(false)
                .missed(false)
                .build();
    }

    public static Umbrella ofUpdated(long id, UmbrellaModifyRequest umbrellaModifyRequest, StoreMeta storeMeta) {

        return Umbrella.builder()
                .id(id)
                .storeMeta(storeMeta)
                .uuid(umbrellaModifyRequest.getUuid())
                .rentable(umbrellaModifyRequest.isRentable())
                .deleted(false)
                .missed(umbrellaModifyRequest.isMissed())
                .build();
    }
}
