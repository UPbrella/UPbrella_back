package upbrella.be.slack.service

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod.POST
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import upbrella.be.config.SlackBotConfig
import upbrella.be.rent.entity.ConditionReport
import upbrella.be.rent.entity.History
import upbrella.be.rent.entity.ImprovementReport
import upbrella.be.user.entity.User

@Service
class SlackAlarmService(
    private val slackBotConfig: SlackBotConfig,
    private val restTemplate: RestTemplate
) {

    fun notifyRent(user: User, history: History) {
        val message = buildString {
            append("*우산 대여 알림: 입금을 확인해주세요.*\n\n")
            append("사용자 ID : ${user.id}\n")
            append("예금주 이름 : ${user.name}\n")
            append("대여 지점 이름 : ${history.rentStoreMeta.name}\n")
        }
        send(message)
    }

    fun notifyReturn(user: User, history: History, unrefundedCount: Long) {
        val message = buildString {
            append("*우산 반납 알림: 보증금을 환급해주세요.*\n\n")
            append("사용자 ID : ${user.id}\n")
            append("대여 지점 이름 : ${history.rentStoreMeta.name}\n")
            append("대여 시각: ${history.rentedAt}\n")
            append("반납 지점 이름 : ${history.returnStoreMeta?.name}\n")
            append("반납 시각: ${history.returnedAt}\n")
            append("*잔여 환급 대기 건수* : $unrefundedCount")
        }
        send(message)
    }

    private fun send(message: String) {
        val request = mutableMapOf<String, Any>(
            "text" to message
        )
        val entity = HttpEntity(request)
        restTemplate.exchange(slackBotConfig.webHookUrl, POST, entity, String::class.java)
    }

    fun notifyConditionReport(conditionReport: ConditionReport) {
        val message = buildString {
            append("*우산 상태 신고 접수*\n\n")
            append("우산 ID : ${conditionReport.history.umbrella.id}\n")
            append("대여 지점 이름 : ${conditionReport.history.rentStoreMeta.name}\n")
            append("신고 내용 : ${conditionReport.content}\n")
        }
        send(message)
    }

    fun notifyImprovementReport(improvementReport: ImprovementReport) {
        val message = buildString {
            append("*우산 개선 사항 신고가 접수되었습니다.*\n\n")
            append("우산 ID : ${improvementReport.history.umbrella.id}\n")
            append("대여 지점 이름 : ${improvementReport.history.rentStoreMeta.name}\n")
            append("대여 시각: ${improvementReport.history.rentedAt}\n")
            append("반납 지점 이름 : ${improvementReport.history.returnStoreMeta?.name}\n")
            append("반납 시각: ${improvementReport.history.returnedAt}\n")
            append("신고 내용 : ${improvementReport.content}\n")
        }
        send(message)
    }
}
