package upbrella.be.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import upbrella.be.config.interceptor.AdminInterceptor
import upbrella.be.config.interceptor.LoginInterceptor

import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.Ordered.LOWEST_PRECEDENCE

@Configuration
@Profile("!test")
class AuthConfig(
    private val loginInterceptor: LoginInterceptor,
    private val adminInterceptor: AdminInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loginInterceptor)
            .order(HIGHEST_PRECEDENCE)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/users/login/**",
                "/users/oauth/login/**",
                "/users/join/**",
                "/auth/apple/**",
                "/stores/**",
                "/index.html",
                "/error/**",
                "/api/error/**",
                "/docs/**",
                "/nGrinder/**")

        registry.addInterceptor(adminInterceptor)
            .order(LOWEST_PRECEDENCE)
            .addPathPatterns("/admin/**")
            .excludePathPatterns(
                "/users/login/**",
                "/users/oauth/login/**",
                "/users/join/**",
                "/index.html",
                "/error/**",
                "/api/error/**",
                "/docs/**")
    }
}
