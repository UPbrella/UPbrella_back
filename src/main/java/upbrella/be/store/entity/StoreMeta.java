package upbrella.be.store.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
}
