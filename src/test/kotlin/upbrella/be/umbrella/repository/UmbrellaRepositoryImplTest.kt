package upbrella.be.umbrella.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.config.QueryDslTestConfig
import upbrella.be.store.entity.ClassificationType
import javax.persistence.EntityManager

@Import(QueryDslTestConfig::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UmbrellaRepositoryImplTest {

    @Autowired
    private lateinit var umbrellaRepository: UmbrellaRepository

    @Autowired
    private lateinit var em: EntityManager

    private var storeMetaId: Long = 0

    @BeforeEach
    fun setUp() {
        val classification = FixtureBuilderFactory.builderClassification()
            .set("type", ClassificationType.CLASSIFICATION)
            .set("id", null)
            .sample()

        val subClassification = FixtureBuilderFactory.builderClassification()
            .set("type", ClassificationType.SUB_CLASSIFICATION)
            .set("id", null)
            .sample()

        em.persist(classification)
        em.persist(subClassification)
        em.flush()

        val storeMeta = FixtureBuilderFactory.builderStoreMeta()
            .set("id", null)
            .set("classification", classification)
            .set("subClassification", subClassification)
            .set("businessHours", null)
            .sample()

        em.persist(storeMeta)
        em.flush()

        // 대여 가능 우산 3개 생성
        repeat(3) {
            val umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", null)
                .set("storeMeta", storeMeta)
                .set("rentable", true)
                .set("deleted", false)
                .set("missed", false)
                .sample()
            em.persist(umbrella)
            em.flush()
        }

        // 대여 중 우산 2개 생성
        repeat(2) {
            val umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", null)
                .set("storeMeta", storeMeta)
                .set("rentable", false)
                .set("deleted", false)
                .set("missed", false)
                .sample()
            em.persist(umbrella)
            em.flush()
        }

        // 분실 우산 1개 생성
        repeat(1) {
            val umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", null)
                .set("storeMeta", storeMeta)
                .set("rentable", false)
                .set("deleted", false)
                .set("missed", true)
                .sample()
            em.persist(umbrella)
            em.flush()
        }

        storeMetaId = storeMeta.id!!
    }

    @Test
    fun countAllUmbrellas() {
        assertThat(umbrellaRepository.countAllUmbrellas())
            .isEqualTo(6)
    }

    @Test
    fun countRentableUmbrellas() {
        assertThat(umbrellaRepository.countRentableUmbrellas())
            .isEqualTo(3)
    }

    @Test
    fun countRentedUmbrellas() {
        assertThat(umbrellaRepository.countRentedUmbrellas())
            .isEqualTo(2)
    }

    @Test
    fun countMissingUmbrellas() {
        assertThat(umbrellaRepository.countMissingUmbrellas())
            .isEqualTo(1)
    }

    @Test
    fun countRentableUmbrellasByStore() {
        assertThat(umbrellaRepository.countRentableUmbrellasByStore(storeMetaId))
            .isEqualTo(3)
    }

    @Test
    fun countRentedUmbrellasByStore() {
        assertThat(umbrellaRepository.countRentedUmbrellasByStore(storeMetaId))
            .isEqualTo(2)
    }

    @Test
    fun countAllUmbrellasByStore() {
        assertThat(umbrellaRepository.countAllUmbrellasByStore(storeMetaId))
            .isEqualTo(6)
    }

    @Test
    fun countMissingUmbrellasByStore() {
        assertThat(umbrellaRepository.countMissingUmbrellasByStore(storeMetaId))
            .isEqualTo(1)
    }
}
