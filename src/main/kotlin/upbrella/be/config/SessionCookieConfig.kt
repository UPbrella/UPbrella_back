package upbrella.be.config

import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.servlet.SessionCookieConfig

@Configuration
@Profile("prod")
class SessionCookieConfig {

    @Bean
    fun cookieSameSiteSupplier(): CookieSameSiteSupplier {
        return CookieSameSiteSupplier.ofNone()
    }

    @Bean
    fun servletContextInitializer(): ServletContextInitializer {
        return ServletContextInitializer { servletContext ->
            val sessionCookieConfig: SessionCookieConfig = servletContext.sessionCookieConfig
            sessionCookieConfig.setSecure(true)
            sessionCookieConfig.setHttpOnly(true)
            sessionCookieConfig.setPath("/")
        }
    }
}
