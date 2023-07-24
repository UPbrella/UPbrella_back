package upbrella.be.store.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreMeta {

    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String thumbnail;
    private boolean activated;
    private boolean deleted;
}
