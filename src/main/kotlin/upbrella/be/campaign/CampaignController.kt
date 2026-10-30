package upbrella.be.campaign

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.util.CustomResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@RestController
class CampaignController(private val service: CampaignService) {
    @GetMapping("/campaign")
    fun dashboard(session: HttpSession) = CustomResponse("success", 200, "스탬프 조회", service.dashboard(userId(session)))

    @PostMapping("/campaign/survey")
    fun survey(session: HttpSession, @Valid @RequestBody request: SurveyRequest) =
        CustomResponse("success", 200, "설문 제출 완료", service.submitSurvey(userId(session), request))

    @GetMapping("/admin/campaign/settings")
    fun settings() = CustomResponse("success", 200, "투어 설정", service.settings())

    @PatchMapping("/admin/campaign/settings")
    fun settings(@Valid @RequestBody request: SettingsRequest) =
        CustomResponse("success", 200, "투어 설정 저장", service.updateSettings(request))

    @GetMapping("/admin/campaign/awards")
    fun awards() = CustomResponse("success", 200, "지급 내역", service.allAwards())

    @GetMapping("/admin/campaign/awards.csv", produces = ["text/csv;charset=UTF-8"])
    fun export(): ResponseEntity<String> {
        val rows = service.allAwards().joinToString("\n") { "${it.id},${it.userId},${it.mission},${it.historyId ?: ""},${it.amount},${it.awardedAt}" }
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=stamp-awards.csv")
            .body("\uFEFFid,user_id,mission,history_id,amount,awarded_at\n$rows")
    }

    private fun userId(session: HttpSession) = (session.getAttribute("user") as SessionUser).id
}
