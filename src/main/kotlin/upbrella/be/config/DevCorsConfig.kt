package upbrella.be.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Profile("dev")
@Configuration
class DevCorsConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",
                "https://upbrella-front.vercel.app",
                "https://dev.upbrella.link"
            )
            .allowedMethods(
                HttpMethod.GET.name,
                HttpMethod.POST.name,
                HttpMethod.PATCH.name,
                HttpMethod.DELETE.name,
                HttpMethod.HEAD.name,
                HttpMethod.OPTIONS.name
            )
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
