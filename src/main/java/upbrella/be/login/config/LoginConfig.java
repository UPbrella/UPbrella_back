package upbrella.be.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class LoginConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
