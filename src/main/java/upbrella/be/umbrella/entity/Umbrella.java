package upbrella.be.umbrella.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.StoreMeta;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Umbrella {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "store_meta_id")
    private StoreMeta storeMeta;
    private long uuid;
    private boolean rentable;
    private boolean deleted;

    public static Umbrella createOf(StoreMeta storeMeta, long uuid, boolean rentable) {

        return new Umbrella(null, storeMeta, uuid, rentable, false);
    }
}
