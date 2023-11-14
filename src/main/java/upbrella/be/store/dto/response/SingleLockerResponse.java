package upbrella.be.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.rent.entity.Locker;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleLockerResponse {

    private long id;
    private long storeMetaId;
    private String secretKey;

    public static SingleLockerResponse fromLocker(Locker locker) {

        return SingleLockerResponse.builder()
                .id(locker.getId())
                .storeMetaId(locker.getStoreMeta().getId())
                .secretKey(locker.getSecretKey())
                .build();
    }
}
