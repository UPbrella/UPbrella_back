package upbrella.be.store.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.config.QueryDslTestConfig;
import upbrella.be.store.entity.*;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(QueryDslTestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StoreDetailRepositoryImplTest {

    @Autowired
    private StoreDetailRepository storeDetailRepository;

    @Autowired
    private EntityManager em;
    private StoreMeta expectedStoreMeta;
    private StoreDetail expectedStoreDetail;

    @BeforeEach
    void setUp() {

        // given
        Classification classification = FixtureBuilderFactory.builderClassification()
                .set("type", ClassificationType.CLASSIFICATION)
                .set("id", null).sample();

        Classification subClassification = FixtureBuilderFactory.builderClassification()
                .set("type", ClassificationType.SUB_CLASSIFICATION)
                .set("id", null).sample();

        em.persist(classification);
        em.persist(subClassification);
        em.flush();


        expectedStoreMeta = FixtureBuilderFactory.builderStoreMeta()
                .set("id", null)
                .set("classification", classification)
                .set("subClassification", subClassification)
                .set("businessHours", null)
                .set("latitude", 30.0)
                .set("longitude", 50.0)
                .set("delete", false).sample();

        em.persist(expectedStoreMeta);
        em.flush();

        expectedStoreDetail = StoreDetail.builder()
                .addressDetail("주소 상세")
                .address("주소")
                .storeMeta(expectedStoreMeta)
                .build();

        BusinessHour businessHour = FixtureBuilderFactory.builderBusinessHour()
                .set("id", null)
                .set("storeMeta", expectedStoreMeta).sample();

        em.persist(expectedStoreDetail);
        em.persist(businessHour);
        em.flush();
    }

    @Test
    @DisplayName("모든 협업지점의 상세 정보를 조회한다.")
    void findAllStores() {

        // when
        List<StoreDetail> stores = storeDetailRepository.findAllStores();

        // then
        Assertions.assertAll(
                () -> assertThat(stores.size()).isEqualTo(1),
                () -> assertThat(stores.get(0))
                        .usingRecursiveComparison()
                        .isEqualTo(expectedStoreDetail));
    }

    @Test
    @DisplayName("협업지점의 메타 정보 고유번호에 해당하는 상세 정보를 조회한다.")
    void findByStoreMetaIdUsingFetchJoin() {

        // when
        Optional<StoreDetail> storeDetail = storeDetailRepository.findByStoreMetaIdUsingFetchJoin(expectedStoreMeta.getId());

        // then
        Assertions.assertAll(
                () -> assertThat(storeDetail).isPresent(),
                () -> assertThat(storeDetail.get())
                        .usingRecursiveComparison()
                        .isEqualTo(expectedStoreDetail)
        );

    }
}
