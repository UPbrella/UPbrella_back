package upbrella.be.slack

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod.POST
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import upbrella.be.config.SlackBotConfig
import upbrella.be.rent.entity.History
import upbrella.be.rent.entity.ImprovementReport
import upbrella.be.slack.dto.service.input.NotifyConditionReportInput
import upbrella.be.slack.dto.service.input.NotifyRentInput
import upbrella.be.user.entity.User

@Service
class SlackAlarmService(
    private val slackBotConfig: SlackBotConfig,
    private val restTemplate: RestTemplate
) {

    fun notifyRent(input: NotifyRentInput) {
        val message = buildString {
            append("*우산 대여 알림: 입금을 확인해주세요.*\n\n")
            append("사용자 ID : ${input.userId}\n")
            append("예금주 이름 : ${input.userName}\n")
            append("대여 지점 이름 : ${input.rentStoreName}\n")
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

    fun notifyConditionReport(input: NotifyConditionReportInput) {
        val message = buildString {
            append("*우산 상태 신고 접수*\n\n")
            append("우산 ID : ${input.umbrellaId}\n")
            append("대여 지점 이름 : ${input.rentStoreName}\n")
            append("신고 내용 : ${input.content}\n")
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

    private fun send(message: String) {
        val request = mutableMapOf<String, Any>(
            "text" to message
        )
        val entity = HttpEntity(request)
        restTemplate.exchange(slackBotConfig.webHookUrl, POST, entity, String::class.java)
    }
}
