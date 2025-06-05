package upbrella.be.rent.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.ArgumentMatchers.any
import org.mockito.junit.jupiter.MockitoExtension
import upbrella.be.rent.entity.ImprovementReport
import upbrella.be.rent.repository.ImprovementReportRepository
import upbrella.be.rent.entity.History
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.user.entity.User
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ImprovementReportServiceTest {

    @Mock
    private lateinit var improvementReportRepository: ImprovementReportRepository

    @InjectMocks
    private lateinit var improvementReportService: ImprovementReportService

    private lateinit var history: History

    @BeforeEach
    fun setUp() {
        val storeMeta = StoreMeta(
            name = "store",
            activated = false,
            category = "cat",
        )
        val umbrella = Umbrella(
            storeMeta = storeMeta,
            uuid = 1L,
            rentable = true,
            deleted = false,
            createdAt = LocalDateTime.now(),
            etc = null,
            missed = false,
        )
        val user = User(0L, "name", "010", "email", false, null, null, 1L)
        history = History(
            umbrella = umbrella,
            user = user,
            rentStoreMeta = storeMeta,
        )
    }

    @Test
    @DisplayName("빈 내용일 경우 레포지토리에 저장하지 않는다")
    fun doNotSaveWhenContentBlank() {
        // given
        val blankReport = ImprovementReport(history = history, content = " ")

        // when
        improvementReportService.save(blankReport)

        // then
        then(improvementReportRepository).should(times(0)).save(any(ImprovementReport::class.java))
    }
}
