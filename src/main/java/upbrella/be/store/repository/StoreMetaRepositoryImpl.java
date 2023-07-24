package upbrella.be.store.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import upbrella.be.store.dto.response.*;
import upbrella.be.store.entity.*;

import java.util.List;
import java.util.stream.Collectors;

import static upbrella.be.store.entity.QStoreDetail.storeDetail;
import static upbrella.be.store.entity.QStoreImage.storeImage;
import static upbrella.be.store.entity.QStoreMeta.storeMeta;

@RequiredArgsConstructor
public class StoreMetaRepositoryImpl implements StoreMetaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SingleStoreResponse> findAllStore() {
        List<StoreDetail> storeDetails = queryFactory
                .selectFrom(storeDetail)
                .fetch();

        return storeDetails.stream()
                .map(this::convertToStoreResponse)
                .collect(Collectors.toList());
    }

    private SingleStoreResponse convertToStoreResponse(StoreDetail storeDetail) {
        StoreMeta storeMeta = storeDetail.getStoreMeta();

        List<StoreImage> storeImages = queryFactory
                .selectFrom(storeImage)
                .where(storeImage.storeDetail.eq(storeDetail))
                .fetch();

        List<String> imageUrls = storeImages.stream()
                .map(StoreImage::getImageUrl)
                .collect(Collectors.toList());

        SingleClassificationResponse classificationResponse = SingleClassificationResponse.builder()
                .id(storeMeta.getClassification().getId())
                .type(storeMeta.getClassification().getType())
                .name(storeMeta.getClassification().getName())
                .latitude(storeMeta.getClassification().getLatitude())
                .longitude(storeMeta.getClassification().getLongitude())
                .build();

        SingleSubClassificationResponse subClassificationResponse = SingleSubClassificationResponse.builder()
                .id(storeMeta.getSubClassification().getId())
                .type(storeMeta.getSubClassification().getType())
                .name(storeMeta.getSubClassification().getName())
                .build();

        return SingleStoreResponse.builder()
                .id(storeMeta.getId())
                .name(storeMeta.getName())
                .category(storeMeta.getCategory())
                .classification(classificationResponse)
                .subClassification(subClassificationResponse)
                .activateStatus(storeMeta.isActivated())
                .address(storeDetail.getAddress())
                .umbrellaLocation(storeDetail.getUmbrellaLocation())
                .businessHours(storeDetail.getWorkingHour())
                .contactNumber(storeDetail.getContactInfo())
                .instagramId(storeDetail.getInstaUrl())
                .latitude(storeMeta.getLatitude())
                .longitude(storeMeta.getLongitude())
                .content(storeDetail.getContent())
                .imageUrls(imageUrls)
                .build();
    }

    public List<SingleStoreResponse> findAll() {

        QStoreImage qStoreImageSub = new QStoreImage("storeImageSub");


        queryFactory.select(new QSingleStoreResponse(
                        storeMeta.id,
                        storeMeta.name,
                        storeMeta.category,
                        new QSingleClassificationResponse(
                                storeMeta.classification.id,
                                storeMeta.classification.type,
                                storeMeta.classification.name,
                                storeMeta.classification.latitude,
                                storeMeta.classification.longitude
                        ),
                        new QSingleSubClassificationResponse(
                                storeMeta.subClassification.id,
                                storeMeta.subClassification.type,
                                storeMeta.subClassification.name
                        ),
                        storeMeta.activated,
                        storeDetail.address,
                        storeDetail.umbrellaLocation,
                        storeDetail.workingHour,
                        storeDetail.contactInfo,
                        storeDetail.instaUrl,
                        storeMeta.latitude,
                        storeMeta.longitude,
                        storeDetail.content,
                // select 절의 서브쿼리의 결과는 스칼라값만 가능해서 List<String> 조회가 불가능
                // OneToMany 관계로 변경하면 가능
                JPAExpressions.select(qStoreImageSub.imageUrl)
                        .from(qStoreImageSub)
                        .where(qStoreImageSub.storeDetail.eq(storeDetail))
                ))

                .from(storeMeta)
                .fetch();

        return null;
    }
}
