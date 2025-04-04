package upbrella.be.slack.service

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod.POST
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import upbrella.be.config.SlackBotConfig

@Service
class SlackAlarmService(
    private val slackBotConfig: SlackBotConfig,
    private val restTemplate: RestTemplate
) {

    fun notifyReturn(unrefundedCount: Long) {
        val message = buildString {
            append("*우산이 반납되었습니다. 보증금을 환급해주세요.*\n\n")
            append("*환급 대기 건수* : ")
            append(unrefundedCount)
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
