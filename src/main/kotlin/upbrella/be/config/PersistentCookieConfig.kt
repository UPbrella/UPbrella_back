package upbrella.be.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.session.web.http.DefaultCookieSerializer

@Configuration
@Profile("home-dev", "prod")
class PersistentCookieConfig {
    @Bean
    fun cookieSerializer(
        @Value("\${SESSION_COOKIE_SECURE:true}") secure: Boolean,
        @Value("\${upbrella.cookie-path:/}") path: String,
        @Value("\${upbrella.session-cookie-name:JSESSIONID}") name: String,
        @Value("\${upbrella.session-same-site:None}") sameSite: String
    ) = DefaultCookieSerializer().apply {
        setCookieName(name)
        setCookiePath(path)
        setUseSecureCookie(secure)
        setUseHttpOnlyCookie(true)
        setSameSite(sameSite)
        setCookieMaxAge(30 * 24 * 60 * 60)
    }
}
