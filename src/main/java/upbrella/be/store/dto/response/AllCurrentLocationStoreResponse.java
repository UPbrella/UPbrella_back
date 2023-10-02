package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllCurrentLocationStoreResponse {

    private List<SingleCurrentLocationStoreResponse> stores;

    public static AllCurrentLocationStoreResponse ofCreate(List<SingleCurrentLocationStoreResponse> singleCurrentLocationStoreResponses) {

        return AllCurrentLocationStoreResponse.builder()
                .stores(singleCurrentLocationStoreResponses)
                .build();
    }
}
