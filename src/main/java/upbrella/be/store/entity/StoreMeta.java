package upbrella.be.store.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class StoreMeta {

    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String thumbnail;
    private boolean activated;
    private boolean deleted;
}
