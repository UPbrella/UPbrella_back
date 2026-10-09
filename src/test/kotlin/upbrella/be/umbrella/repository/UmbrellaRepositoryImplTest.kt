package upbrella.be.umbrella.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.config.QueryDslTestConfig
import upbrella.be.store.entity.ClassificationType
import upbrella.be.umbrella.entity.UmbrellaStatus
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
                .set("status", UmbrellaStatus.AVAILABLE)
                .set("deleted", false)
                .sample()
            em.persist(umbrella)
            em.flush()
        }

        // 대여 중 우산 2개 생성
        repeat(2) {
            val umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", null)
                .set("storeMeta", storeMeta)
                .set("status", UmbrellaStatus.RENTED)
                .set("deleted", false)
                .sample()
            em.persist(umbrella)
            em.flush()
        }

        // 분실 우산 1개 생성
        repeat(1) {
            val umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", null)
                .set("storeMeta", storeMeta)
                .set("status", UmbrellaStatus.LOST)
                .set("deleted", false)
                .sample()
            em.persist(umbrella)
            em.flush()
        }

        storeMetaId = storeMeta.id!!
    }

    @Test
    @DisplayName("삭제되지 않은 우산을 상태별로 센다")
    fun countUmbrellasByStatus() {
        assertThat(umbrellaRepository.countUmbrellasByStatus())
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    UmbrellaStatus.AVAILABLE to 3L,
                    UmbrellaStatus.RENTED to 2L,
                    UmbrellaStatus.LOST to 1L
                )
            )
    }

    @Test
    @DisplayName("지점의 삭제되지 않은 우산을 상태별로 센다")
    fun countUmbrellasByStatusAndStore() {
        assertThat(umbrellaRepository.countUmbrellasByStatusAndStore(storeMetaId))
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    UmbrellaStatus.AVAILABLE to 3L,
                    UmbrellaStatus.RENTED to 2L,
                    UmbrellaStatus.LOST to 1L
                )
            )
    }

    @Test
    @DisplayName("다른 지점의 우산은 세지 않는다")
    fun countUmbrellasByStatusAndOtherStore() {
        assertThat(umbrellaRepository.countUmbrellasByStatusAndStore(storeMetaId + 1))
            .isEmpty()
    }
}
