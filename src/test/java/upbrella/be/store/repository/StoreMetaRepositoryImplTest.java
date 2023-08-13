package upbrella.be.store.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.config.QueryDslTestConfig;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;
import upbrella.be.store.entity.StoreMeta;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(QueryDslTestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StoreMetaRepositoryImplTest {

    @Autowired
    private StoreMetaRepository storeMetaRepository;

    @Autowired
    private EntityManager em;
    private StoreMeta expectedStoreMeta;

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

        BusinessHour businessHour = FixtureBuilderFactory.builderBusinessHour()
                .set("id", null)
                .set("storeMeta", expectedStoreMeta).sample();


        em.persist(businessHour);
        em.flush();
    }

    @Test
    @DisplayName("지정한 위도, 경도 범위 내의 협업 지점의 메타 정보를 조회한다.")
    void findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween() {

        // when
        List<StoreMeta> storeMetas = storeMetaRepository.findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(20.0, 40.0, 40.0, 60.0);

        // then
        assertAll(
                () -> assertThat(storeMetas)
                        .usingRecursiveComparison()
                        .isEqualTo(List.of(expectedStoreMeta)),
                () -> assertThat(storeMetas.size()).isEqualTo(1)
        );
    }
}
