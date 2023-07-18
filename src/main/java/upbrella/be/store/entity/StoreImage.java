package upbrella.be.store.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class StoreImage {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    @JoinColumn(name = "store_detail_id")
    private StoreDetail storeDetail;
    private String imageUrl;
}
