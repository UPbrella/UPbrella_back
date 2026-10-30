package upbrella.be.campaign

import org.springframework.context.event.EventListener
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import upbrella.be.rent.entity.History
import upbrella.be.rent.repository.RentRepository
import upbrella.be.user.repository.UserRepository
import java.time.Clock
import java.time.LocalDateTime

@Service
class CampaignService(
    private val settingsRepository: CampaignSettingsRepository,
    private val ledgerRepository: StampLedgerRepository,
    private val surveyRepository: CampaignSurveyRepository,
    private val userRepository: UserRepository,
    private val rentRepository: RentRepository,
    private val clock: Clock
) {
    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun initialize() {
        if (!settingsRepository.existsById(1)) settingsRepository.save(CampaignSettings())
    }

    fun settings(): CampaignSettings = settingsRepository.findById(1).orElseGet { CampaignSettings() }

    @Transactional
    fun updateSettings(request: SettingsRequest): CampaignSettings {
        val settings = settings()
        settings.title = request.title
        settings.enabled = request.enabled
        settings.returnDeadlineDays = request.returnDeadlineDays
        return settingsRepository.save(settings)
    }

    @Transactional
    fun rentalCompleted(history: History) {
        val settings = settings()
        if (!settings.enabled) return
        if (rentRepository.countByUserId(history.user.id!!) == 1L) award(history.user.id!!, "FIRST_RENT", history.id)
    }

    @Transactional
    fun returnCompleted(history: History) {
        val settings = settings()
        if (!settings.enabled) return
        val returnedAt = history.returnedAt ?: return
        if (!returnedAt.isAfter(history.rentedAt.plusDays(settings.returnDeadlineDays.toLong()))) {
            award(history.user.id!!, "ON_TIME_RETURN", history.id)
        }
    }

    @Transactional
    fun submitSurvey(userId: Long, request: SurveyRequest): Map<String, Any> {
        userRepository.findLockedById(userId).orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED) }
        val settings = settings()
        if (!settings.enabled) throw ResponseStatusException(HttpStatus.CONFLICT, "진행 중인 스탬프 투어가 없습니다.")
        if (!surveyRepository.existsByUserIdAndCampaignKey(userId, settings.campaignKey)) {
            surveyRepository.save(CampaignSurvey(userId, settings.campaignKey, request.easeOfUse,
                request.satisfaction, request.feedback.trim(), LocalDateTime.now(clock)))
            award(userId, "SURVEY", null)
        }
        return dashboard(userId)
    }

    fun dashboard(userId: Long): Map<String, Any> {
        val settings = settings()
        val ledger = ledgerRepository.findAllByUserIdAndCampaignKeyOrderByAwardedAtDesc(userId, settings.campaignKey)
        return mapOf("settings" to settings, "balance" to ledger.sumOf { it.amount }, "ledger" to ledger,
            "missions" to listOf(
                mapOf("code" to "FIRST_RENT", "title" to "첫 대여 완료", "description" to "처음 우산을 대여하면 스탬프 1개", "completed" to ledger.any { it.mission == "FIRST_RENT" }),
                mapOf("code" to "ON_TIME_RETURN", "title" to "기한 내 반납", "description" to "${settings.returnDeadlineDays}일 이내 반납하면 스탬프 1개 · 사용자당 1회", "completed" to ledger.any { it.mission == "ON_TIME_RETURN" }),
                mapOf("code" to "SURVEY", "title" to "1분 설문", "description" to "서비스 이용 설문을 제출하면 스탬프 1개", "completed" to ledger.any { it.mission == "SURVEY" })
            ))
    }

    fun allAwards(): List<StampLedger> = ledgerRepository.findAllByCampaignKeyOrderByAwardedAtDesc(settings().campaignKey)

    private fun award(userId: Long, mission: String, historyId: Long?) {
        userRepository.findLockedById(userId).orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED) }
        val campaignKey = settings().campaignKey
        val key = "$campaignKey:$mission:$userId"
        if (!ledgerRepository.existsByAwardKey(key)) {
            ledgerRepository.save(StampLedger(userId, campaignKey, mission, key, historyId,
                awardedAt = LocalDateTime.now(clock)))
        }
    }
}
