package upbrella.be.config

import org.apache.tomcat.util.http.LegacyCookieProcessor
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.servlet.SessionCookieConfig

@Configuration
@Profile("prod")
class SessionCookieConfig {

    @Bean
    fun cookieProcessorCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        return WebServerFactoryCustomizer { factory ->
            factory.addContextCustomizers(
                org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer { context ->
                    // LegacyCookieProcessor를 사용하면 점으로 시작하는 도메인 허용
                    context.cookieProcessor = LegacyCookieProcessor()
                }
            )
        }
    }

    @Bean
    fun servletContextInitializer(): ServletContextInitializer {
        return ServletContextInitializer { servletContext ->
            val sessionCookieConfig: SessionCookieConfig = servletContext.sessionCookieConfig
            sessionCookieConfig.setSecure(true)
            sessionCookieConfig.setHttpOnly(true)
            sessionCookieConfig.setDomain(".upbrella.co.kr")
            sessionCookieConfig.setPath("/")
        }
    }
}
