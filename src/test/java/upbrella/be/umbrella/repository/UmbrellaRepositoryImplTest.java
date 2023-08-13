package upbrella.be.umbrella.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.config.QueryDslTestConfig;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@Import(QueryDslTestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UmbrellaRepositoryImplTest {

    @Autowired
    private UmbrellaRepository umbrellaRepository;

    @Autowired
    private EntityManager em;

    private long storeMetaId;

    @BeforeEach
    void setUp() {

        Classification classification = FixtureBuilderFactory.builderClassification()
                .set("type", ClassificationType.CLASSIFICATION)
                .set("id", null).sample();

        Classification subClassification = FixtureBuilderFactory.builderClassification()
                .set("type", ClassificationType.SUB_CLASSIFICATION)
                .set("id", null).sample();

        em.persist(classification);
        em.persist(subClassification);
        em.flush();

        StoreMeta storeMeta = FixtureBuilderFactory.builderStoreMeta()
                .set("id", null)
                .set("classification", classification)
                .set("subClassification", subClassification)
                .set("businessHours", null).sample();

        em.persist(storeMeta);
        em.flush();

        // 대여 가능 우산 3개 생성
        for (int i = 0; i < 3; i++) {
            Umbrella umbrella = FixtureBuilderFactory.builderUmbrella()
                    .set("id", null)
                    .set("storeMeta", storeMeta)
                    .set("rentable", true)
                    .set("deleted", false)
                    .set("missed", false)
                    .sample();

            em.persist(umbrella);
            em.flush();
        }

        // 대여 중 우산 2개 생성
        for (int i = 0; i < 2; i++) {
            Umbrella umbrella = FixtureBuilderFactory.builderUmbrella()
                    .set("id", null)
                    .set("storeMeta", storeMeta)
                    .set("rentable", false)
                    .set("deleted", false)
                    .set("missed", false)
                    .sample();

            em.persist(umbrella);
            em.flush();
        }

        // 분실 우산 1개 생성
        for (int i = 0; i < 1; i++) {
            Umbrella umbrella = FixtureBuilderFactory.builderUmbrella()
                    .set("id", null)
                    .set("storeMeta", storeMeta)
                    .set("rentable", false)
                    .set("deleted", false)
                    .set("missed", true)
                    .sample();

            em.persist(umbrella);
            em.flush();
        }
        storeMetaId = storeMeta.getId();

    }

    @Test
    void countAllUmbrellas() {

        assertThat(umbrellaRepository.countAllUmbrellas())
                .isEqualTo(6);
    }

    @Test
    void countRentableUmbrellas() {

        assertThat(umbrellaRepository.countRentableUmbrellas())
                .isEqualTo(3);
    }

    @Test
    void countRentedUmbrellas() {

        assertThat(umbrellaRepository.countRentedUmbrellas())
                .isEqualTo(2);
    }

    @Test
    void countMissingUmbrellas() {

        assertThat(umbrellaRepository.countMissingUmbrellas())
                .isEqualTo(1);
    }

    @Test
    void countRentableUmbrellasByStore() {

        assertThat(umbrellaRepository.countRentableUmbrellasByStore(storeMetaId))
                .isEqualTo(3);
    }

    @Test
    void countRentedUmbrellasByStore() {

        assertThat(umbrellaRepository.countRentedUmbrellasByStore(storeMetaId))
                .isEqualTo(2);
    }

    @Test
    void countAllUmbrellasByStore() {

        assertThat(umbrellaRepository.countAllUmbrellasByStore(storeMetaId))
                .isEqualTo(6);
    }

    @Test
    void countMissingUmbrellasByStore() {

        assertThat(umbrellaRepository.countMissingUmbrellasByStore(storeMetaId))
                .isEqualTo(1);
    }
}

