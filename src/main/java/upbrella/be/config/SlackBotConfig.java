package upbrella.be.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SlackBotConfig {

    @Value("${SLACK_WEB_HOOK_URL}")
    private String webHookUrl;
}
