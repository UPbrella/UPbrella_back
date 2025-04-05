package upbrella.be.store.repository

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.config.QueryDslTestConfig
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.ClassificationType
import javax.persistence.EntityManager

@Import(QueryDslTestConfig::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StoreMetaRepositoryImplTest {

    @Autowired
    private lateinit var storeMetaRepository: StoreMetaRepository

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var expectedStoreMeta: StoreMetaWithUmbrellaCount
    private lateinit var classification: Classification

    @BeforeEach
    fun setUp() {
        // given
        classification = FixtureBuilderFactory.builderClassification()
            .set("type", ClassificationType.CLASSIFICATION)
            .set("id", null).sample()

        val subClassification = FixtureBuilderFactory.builderClassification()
            .set("type", ClassificationType.SUB_CLASSIFICATION)
            .set("id", null).sample()

        em.persist(classification)
        em.persist(subClassification)
        em.flush()

        val storeMeta = FixtureBuilderFactory.builderStoreMeta()
            .set("id", null)
            .set("classification", classification)
            .set("subClassification", subClassification)
            .set("businessHours", null)
            .set("latitude", 30.0)
            .set("longitude", 50.0)
            .set("delete", false).sample()

        em.persist(storeMeta)
        em.flush()

        val businessHour = FixtureBuilderFactory.builderBusinessHour()
            .set("id", null)
            .set("storeMeta", storeMeta).sample()

        em.persist(businessHour)
        em.flush()

        val umbrella = FixtureBuilderFactory.builderUmbrella()
            .set("id", null)
            .set("rentable", true)
            .set("missed", false)
            .set("storeMeta", storeMeta)
            .sample()

        em.persist(umbrella)
        em.flush()

        expectedStoreMeta =
            StoreMetaWithUmbrellaCount(
                storeMeta,
                1L
            )
    }

    @Test
    @DisplayName("지정한 대분류 고유번호에 해당하는 협업 지점의 메타 정보를 조회한다.")
    fun findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween() {
        // given

        // when
        val storeMetas = storeMetaRepository.findAllStoresByClassification(classification.id!!)

        // then
        assertAll(
            {
                assertThat(storeMetas).usingRecursiveComparison()
                    .isEqualTo(listOf(expectedStoreMeta))
            },
            { assertThat(storeMetas.size).isEqualTo(1) }
        )
    }
}