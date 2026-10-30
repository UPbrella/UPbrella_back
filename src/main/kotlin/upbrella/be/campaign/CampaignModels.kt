package upbrella.be.campaign

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.*

@Entity
class CampaignSettings(
    @Id val id: Long = 1,
    var title: String = "업브렐라 스탬프 투어",
    var campaignKey: String = "26-2",
    var enabled: Boolean = false,
    var returnDeadlineDays: Int = 14
)

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["award_key"])])
class StampLedger(
    val userId: Long,
    val campaignKey: String,
    val mission: String,
    @Column(name = "award_key", length = 120) val awardKey: String,
    val historyId: Long? = null,
    val amount: Int = 1,
    val awardedAt: LocalDateTime,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["campaign_key", "user_id"])])
class CampaignSurvey(
    @Column(name = "user_id") val userId: Long,
    @Column(name = "campaign_key") val campaignKey: String,
    val easeOfUse: Int,
    val satisfaction: Int,
    @Column(length = 1000) val feedback: String,
    val submittedAt: LocalDateTime,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)

@Entity
class Banner(
    var title: String,
    @Column(length = 1000) var imageUrl: String,
    @Column(length = 1000) var targetUrl: String,
    var displayOrder: Int = 0,
    var enabled: Boolean = true,
    var startsAt: LocalDateTime? = null,
    var endsAt: LocalDateTime? = null,
    var deleted: Boolean = false,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)

interface CampaignSettingsRepository : JpaRepository<CampaignSettings, Long>
interface StampLedgerRepository : JpaRepository<StampLedger, Long> {
    fun existsByAwardKey(awardKey: String): Boolean
    fun findAllByUserIdAndCampaignKeyOrderByAwardedAtDesc(userId: Long, campaignKey: String): List<StampLedger>
    fun findAllByCampaignKeyOrderByAwardedAtDesc(campaignKey: String): List<StampLedger>
}
interface CampaignSurveyRepository : JpaRepository<CampaignSurvey, Long> {
    fun existsByUserIdAndCampaignKey(userId: Long, campaignKey: String): Boolean
}
interface BannerRepository : JpaRepository<Banner, Long> {
    fun findAllByDeletedIsFalseOrderByDisplayOrderAscIdAsc(): List<Banner>
}

data class SurveyRequest(
    @field:Min(1) @field:Max(5) val easeOfUse: Int,
    @field:Min(1) @field:Max(5) val satisfaction: Int,
    @field:Size(max = 1000) val feedback: String = ""
)
data class SettingsRequest(
    @field:NotBlank @field:Size(max = 100) val title: String,
    val enabled: Boolean,
    @field:Min(1) @field:Max(90) val returnDeadlineDays: Int
)
data class BannerRequest(
    @field:NotBlank @field:Size(max = 100) val title: String,
    @field:NotBlank @field:Size(max = 1000) val imageUrl: String,
    @field:NotBlank @field:Size(max = 1000) val targetUrl: String,
    val displayOrder: Int = 0,
    val enabled: Boolean = true,
    val startsAt: LocalDateTime? = null,
    val endsAt: LocalDateTime? = null
)
