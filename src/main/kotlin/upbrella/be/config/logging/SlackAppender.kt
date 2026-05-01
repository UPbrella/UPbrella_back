package upbrella.be.config.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.core.AppenderBase
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SlackAppender : AppenderBase<ILoggingEvent>() {

    var webhookUrl: String = ""

    override fun append(event: ILoggingEvent) {
        if (webhookUrl.isBlank()) return
        try {
            sendToSlack(formatMessage(event))
        } catch (e: Exception) {
            addError("Slack 로그 전송 실패", e)
        }
    }

    private fun formatMessage(event: ILoggingEvent): String {
        val stackTrace = event.throwableProxy?.let { buildStackTrace(it) } ?: ""
        return buildString {
            append("*[${event.level}]* `${event.loggerName.substringAfterLast('.')}`\n")
            append(event.formattedMessage)
            if (stackTrace.isNotBlank()) append("\n```$stackTrace```")
        }
    }

    private fun buildStackTrace(proxy: IThrowableProxy): String {
        val sb = StringBuilder("${proxy.className}: ${proxy.message}\n")
        proxy.stackTraceElementProxyArray
            ?.take(10)
            ?.forEach { sb.append("\tat ${it.steAsString}\n") }
        if ((proxy.stackTraceElementProxyArray?.size ?: 0) > 10) {
            sb.append("\t... (생략)")
        }
        return sb.toString()
    }

    private fun sendToSlack(message: String) {
        val url = URL(webhookUrl)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.doOutput = true
        val escaped = message
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        val payload = """{"text":"$escaped"}"""
        OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { it.write(payload) }
        conn.responseCode
        conn.disconnect()
    }
}
