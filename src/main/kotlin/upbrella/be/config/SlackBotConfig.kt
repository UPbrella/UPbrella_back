package upbrella.be.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class SlackBotConfig {

    @Value("\${SLACK_WEB_HOOK_URL}")
    lateinit var webHookUrl: String
}
