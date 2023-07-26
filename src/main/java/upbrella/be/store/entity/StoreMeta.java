package upbrella.be.store.entity;

import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String thumbnail;
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

    public void delete() {
        this.deleted = true;
    }
}