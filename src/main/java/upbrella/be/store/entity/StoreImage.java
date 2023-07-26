package upbrella.be.store.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StoreImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_detail_id")
    private StoreDetail storeDetail;
    private String imageUrl;

    public static StoreImage createStoreImage(StoreDetail storeDetail, String imageUrl) {
        return StoreImage.builder()
                .storeDetail(storeDetail)
                .imageUrl(imageUrl)
                .build();
    }
}