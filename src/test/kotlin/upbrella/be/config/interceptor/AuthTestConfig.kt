package upbrella.be.config.interceptor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.Ordered.LOWEST_PRECEDENCE

@Profile("test")
@Configuration
class AuthTestConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var loginInterceptor: LoginInterceptor

    @Autowired
    private lateinit var adminInterceptor: AdminInterceptor

    private val excludedPaths = listOf(
        "/oauth/token",
        "/user/me",
        "/users/login/**",
        "/users/oauth/login/**",
        "/users/join/**",
        "/stores/**",
        "/index.html",
        "/error/**",
        "/api/error/**",
        "/docs/**"
    )

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loginInterceptor)
            .order(HIGHEST_PRECEDENCE)
            .addPathPatterns("/**")
            .excludePathPatterns(excludedPaths)

        registry.addInterceptor(adminInterceptor)
            .order(LOWEST_PRECEDENCE)
            .addPathPatterns("/admin/**")
            .excludePathPatterns(excludedPaths)
    }
}
