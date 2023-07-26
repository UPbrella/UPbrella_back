package upbrella.be.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import upbrella.be.store.entity.StoreImage;

import java.util.ArrayList;
import java.util.List;

@Getter
public class reponse {

    private List<String> images;

    @QueryProjection
    public reponse(List<StoreImage> storeImage) {
        images = new ArrayList<>();

        for (StoreImage image : storeImage) {
            images.add(image.getImageUrl());
        }
    }
}
