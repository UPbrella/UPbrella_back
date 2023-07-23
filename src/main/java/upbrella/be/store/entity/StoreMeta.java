package upbrella.be.store.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import upbrella.be.store.dto.request.CreateStoreRequest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE storeMeta SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class StoreMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String thumbnail;
    private boolean activated;
    private boolean deleted;
    private String classification;
    private String subClassification;
    private String category;
    private double latitude;
    private double longitude;

    @Builder
    private StoreMeta(String name, String thumbnail, boolean activated, boolean deleted, String classification, String subClassification, String category, double latitude, double longitude) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.activated = activated;
        this.deleted = deleted;
        this.classification = classification;
        this.subClassification = subClassification;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static StoreMeta createForSave(CreateStoreRequest request){
        return StoreMeta.builder()
                .name(request.getName())
                .thumbnail(request.getImageUrls().get(0))
                .activated(request.isActivateStatus())
                .deleted(false)
                .classification(request.getClassification())
                .subClassification(request.getSubClassification())
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }
}
