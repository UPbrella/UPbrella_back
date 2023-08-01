package upbrella.be.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.dto.request.CreateClassificationRequest;
import upbrella.be.store.dto.request.CreateSubClassificationRequest;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Classification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private ClassificationType type;
    private String name;
    private Double latitude;
    private Double longitude;

    public static Classification ofCreateClassification(CreateClassificationRequest request) {

        return Classification.builder()
                .name(request.getName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    public static Classification ofCreateSubClassification(CreateSubClassificationRequest request) {

        return Classification.builder()
                .name(request.getName())
                .build();
    }
}