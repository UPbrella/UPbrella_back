package upbrella.be.campaign

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.*
import org.assertj.core.api.Assertions.assertThat
import upbrella.be.user.entity.User
import upbrella.be.user.repository.UserRepository
import upbrella.be.rent.repository.RentRepository
import upbrella.be.rent.entity.History
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.entity.Umbrella
import java.time.*
import java.util.Optional

class CampaignServiceTest {
    private val settings = mock<CampaignSettingsRepository>()
    private val ledger = mock<StampLedgerRepository>()
    private val surveys = mock<CampaignSurveyRepository>()
    private val users = mock<UserRepository>()
    private val rents = mock<RentRepository>()
    private val clock = Clock.fixed(Instant.parse("2026-10-11T00:00:00Z"), ZoneId.of("Asia/Seoul"))
    private val service = CampaignService(settings, ledger, surveys, users, rents, clock)
    private val user = User(socialId = -1, name = "테스트", email = "", id = 1)
    private val store = StoreMeta(name = "테스트", activated = true, category = "테스트", id = 1)
    private val umbrella = Umbrella(store, 77, true, false, LocalDateTime.now(clock), null, false, 1)
    @BeforeEach fun prepare() {
        whenever(settings.findById(1)).thenReturn(Optional.of(CampaignSettings(enabled = true)))
        whenever(users.findLockedById(1)).thenReturn(Optional.of(user))
    }
    private fun history() = History(umbrella = umbrella, user = user, rentStoreMeta = store,
        rentedAt = LocalDateTime.of(2026, 9, 27, 9, 0), id = 1)
    @Test fun `first actual rental earns a single stamp`() {
        whenever(rents.countByUserId(1)).thenReturn(1)
        whenever(ledger.existsByAwardKey("26-2:FIRST_RENT:1")).thenReturn(false, true)
        service.rentalCompleted(history()); service.rentalCompleted(history())
        val captor = argumentCaptor<StampLedger>(); verify(ledger, times(1)).save(captor.capture())
        assertThat(captor.firstValue.mission).isEqualTo("FIRST_RENT")
    }
    @Test fun `second rental does not earn first rental stamp`() {
        whenever(rents.countByUserId(1)).thenReturn(2)
        service.rentalCompleted(history()); verify(ledger, never()).save(any())
    }
    @Test fun `return exactly at deadline earns a stamp but one second late does not`() {
        val h = history(); h.returnedAt = h.rentedAt.plusDays(14)
        service.returnCompleted(h)
        verify(ledger).save(check { assertThat(it.mission).isEqualTo("ON_TIME_RETURN") })
        clearInvocations(ledger)
        h.returnedAt = h.rentedAt.plusDays(14).plusSeconds(1)
        service.returnCompleted(h); verify(ledger, never()).save(any())
    }
    @Test fun `inactive campaign does not award`() {
        whenever(settings.findById(1)).thenReturn(Optional.of(CampaignSettings(enabled = false)))
        val h = history(); h.returnedAt = h.rentedAt.plusDays(1)
        service.rentalCompleted(h); service.returnCompleted(h)
        verifyNoInteractions(ledger)
    }
    @Test fun `survey resubmission does not award twice`() {
        whenever(surveys.existsByUserIdAndCampaignKey(1, "26-2")).thenReturn(false, true)
        whenever(ledger.findAllByUserIdAndCampaignKeyOrderByAwardedAtDesc(1, "26-2")).thenReturn(emptyList())
        whenever(ledger.existsByAwardKey("26-2:SURVEY:1")).thenReturn(false)
        service.submitSurvey(1, SurveyRequest(5, 4, "편했어요")); service.submitSurvey(1, SurveyRequest(1, 1, "다시 제출"))
        verify(surveys, times(1)).save(any()); verify(ledger, times(1)).save(any())
    }
}
