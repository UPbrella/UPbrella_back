package upbrella.be.store.entity;

import lombok.*;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.UpdateStoreRequest;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StoreDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_meta_id")
    private StoreMeta storeMeta;
    private String umbrellaLocation;
    private String workingHour;
    private String instaUrl;
    private String contactInfo;
    private String address;
    private String addressDetail;
    private String content;
    @OneToMany(mappedBy = "storeDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StoreImage> storeImages;


    public static StoreDetail createForSave(CreateStoreRequest request, StoreMeta storeMeta) {

        return StoreDetail.builder()
                .storeMeta(storeMeta)
                .umbrellaLocation(request.getUmbrellaLocation())
                .workingHour(request.getBusinessHour())
                .instaUrl(request.getInstagramId())
                .contactInfo(request.getContactNumber())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .content(request.getContent())
                .build();
    }

    public void updateStore(StoreMeta storeMeta, UpdateStoreRequest request) {

        this.storeMeta = storeMeta;
        this.umbrellaLocation = request.getUmbrellaLocation();
        this.workingHour = request.getBusinessHour();
        this.instaUrl = request.getInstagramId();
        this.contactInfo = request.getContactNumber();
        this.address = request.getAddress();
        this.addressDetail = request.getAddressDetail();
        this.content = request.getContent();
    }

    public List<StoreImage> getSortedStoreImages() {

        return storeImages.stream()
                .sorted((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .collect(Collectors.toList());
    }
}
