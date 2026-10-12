package upbrella.be.rent.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import upbrella.be.config.QueryDslTestConfig
import upbrella.be.rent.dto.request.HistoryFilterRequest
import upbrella.be.rent.entity.History
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.entity.UmbrellaStatus
import upbrella.be.user.entity.User
import java.time.LocalDateTime
import javax.persistence.EntityManager

@Import(QueryDslTestConfig::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RentRepositoryImplTest {

    @Autowired
    private lateinit var rentRepository: RentRepository

    @Autowired
    private lateinit var em: EntityManager

    private val pageable = PageRequest.of(0, 10)
    private var overdueHistoryId: Long = 0

    @BeforeEach
    fun setUp() {
        val storeMeta = StoreMeta(name = "대여점", activated = true, category = "카페")
        em.persist(storeMeta)

        val umbrella = Umbrella(
            storeMeta = storeMeta,
            uuid = 1L,
            status = UmbrellaStatus.RENTED,
            deleted = false,
            createdAt = LocalDateTime.now(),
            etc = null
        )
        em.persist(umbrella)

        val user = User(socialId = 1L, name = "사용자", email = "user@upbrella.com")
        em.persist(user)

        val now = LocalDateTime.now()

        // 반납 기한이 지났는데 반납하지 않음
        val overdue = History(
            umbrella = umbrella,
            user = user,
            rentStoreMeta = storeMeta,
            rentedAt = now.minusDays(History.RETURN_DEADLINE_DAYS).minusHours(1)
        )
        em.persist(overdue)

        // 아직 반납 기한 안
        em.persist(
            History(
                umbrella = umbrella,
                user = user,
                rentStoreMeta = storeMeta,
                rentedAt = now.minusDays(History.RETURN_DEADLINE_DAYS - 1)
            )
        )

        // 반납 기한은 지났지만 반납함
        em.persist(
            History(
                umbrella = umbrella,
                user = user,
                rentStoreMeta = storeMeta,
                returnStoreMeta = storeMeta,
                rentedAt = now.minusDays(20),
                returnedAt = now.minusDays(18)
            )
        )

        em.flush()
        em.clear()
        overdueHistoryId = overdue.id!!
    }

    @Test
    @DisplayName("장기 미반납 필터를 켜면 반납 기한이 지났는데 반납하지 않은 대여 내역만 조회한다")
    fun findOverdueHistories() {
        // given
        val filter = HistoryFilterRequest(overdue = true)

        // when
        val histories = rentRepository.findHistoryInfos(filter, pageable)
        val count = rentRepository.countAll(filter, pageable)

        // then
        assertAll(
            { assertThat(histories.map { it.id }).containsExactly(overdueHistoryId) },
            { assertThat(count).isEqualTo(1L) }
        )
    }

    @Test
    @DisplayName("장기 미반납 필터가 없으면 모든 대여 내역을 조회한다")
    fun findAllHistoriesWithoutOverdueFilter() {
        // given
        val filter = HistoryFilterRequest()

        // when
        val histories = rentRepository.findHistoryInfos(filter, pageable)
        val count = rentRepository.countAll(filter, pageable)

        // then
        assertAll(
            { assertThat(histories).hasSize(3) },
            { assertThat(count).isEqualTo(3L) }
        )
    }
}
