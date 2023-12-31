package upbrella.be.store.entity;

import lombok.*;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.UpdateStoreRequest;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StoreMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private boolean activated;
    private boolean deleted;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classification_id")
    private Classification classification;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_classification_id")
    private Classification subClassification;
    private String category;
    private double latitude;
    private double longitude;
    @OneToMany(mappedBy = "storeMeta")
    private List<BusinessHour> businessHours;

    public static StoreMeta createStoreMetaForSave(CreateStoreRequest request, Classification classification, Classification subClassification) {

        return StoreMeta.builder()
                .name(request.getName())
                .activated(request.isActivateStatus())
                .deleted(false)
                .classification(classification)
                .subClassification(subClassification)
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    public static StoreMeta createStoreMetaForUpdate(UpdateStoreRequest request, Classification classification, Classification subClassification) {

        return StoreMeta.builder()
                .name(request.getName())
                .deleted(false)
                .classification(classification)
                .subClassification(subClassification)
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    public void updateStoreMeta(StoreMeta storeMeta) {

        this.name = storeMeta.getName();
        this.deleted = storeMeta.isDeleted();
        this.classification = storeMeta.getClassification();
        this.subClassification = storeMeta.getSubClassification();
        this.category = storeMeta.getCategory();
        this.latitude = storeMeta.getLatitude();
        this.longitude = storeMeta.getLongitude();
    }

    public void delete() {

        this.classification = null;
        this.subClassification = null;
        this.deleted = true;
    }

    public boolean isOpenStore(LocalDateTime currentTime) {

        List<BusinessHour> businessHours = this.getBusinessHours();

        return businessHours.stream()
                .filter(businessHour -> businessHour.getDate().equals(currentTime.getDayOfWeek()))
                .filter(e -> this.isActivated())
                .anyMatch(businessHour ->
                        currentTime.toLocalTime().isAfter(businessHour.getOpenAt())
                                && currentTime.toLocalTime().isBefore(businessHour.getCloseAt()));
    }

    public void activateStoreStatus() {

        this.activated = true;
    }

    public void inactivateStoreStatus() {

        this.activated = false;
    }
}
