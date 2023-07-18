package upbrella.be.store.entity;

import lombok.Getter;
import org.springframework.data.geo.Point;

import javax.persistence.*;

@Entity
@Getter
public class StoreDetail {

    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    @JoinColumn(name = "store_meta_id")
    private StoreMeta storeMeta;
    private Point location;
    private String workingHour;
    private String instaUrl;
    private String contactInfo;
    private String address;
    private String content;
}
