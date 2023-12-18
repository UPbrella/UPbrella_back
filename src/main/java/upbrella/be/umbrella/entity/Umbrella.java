package upbrella.be.umbrella.entity;

import lombok.*;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest;
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    private String etc;
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
                .createdAt(LocalDateTime.now())
                .etc(umbrellaCreateRequest.getEtc())
                .build();
    }

    public static Umbrella ofUpdated(long id, Umbrella umbrella, UmbrellaModifyRequest umbrellaModifyRequest, StoreMeta storeMeta) {

        return Umbrella.builder()
                .id(id)
                .storeMeta(storeMeta)
                .uuid(umbrellaModifyRequest.getUuid())
                .rentable(umbrellaModifyRequest.isRentable())
                .deleted(false)
                .createdAt(umbrella.getCreatedAt())
                .etc(umbrellaModifyRequest.getEtc())
                .missed(umbrellaModifyRequest.isMissed())
                .build();
    }

    public void update(UmbrellaModifyRequest request, StoreMeta storeMeta) {

        this.storeMeta = storeMeta;
        this.uuid = request.getUuid();
        this.rentable = request.isRentable();
        this.etc = request.getEtc();
        this.missed = request.isMissed();
    }

    public void rentUmbrella() {

        this.rentable = false;
    }

    public void returnUmbrella(StoreMeta storeMeta) {

        this.storeMeta = storeMeta;
        this.rentable = true;
    }

    public boolean validateCannotBeRented() {

        return missed || deleted || !rentable;
    }
}
