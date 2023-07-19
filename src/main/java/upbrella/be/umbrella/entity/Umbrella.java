package upbrella.be.umbrella.entity;

import lombok.Getter;
import upbrella.be.store.entity.StoreMeta;

import javax.persistence.*;

@Entity
@Getter
public class Umbrella {

    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "store_meta_id")
    private StoreMeta storeMeta;
    private long uuid;
    private boolean rentable;
    private boolean deleted;
}
