package upbrella.be.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import upbrella.be.config.interceptor.AdminInterceptor;
import upbrella.be.config.interceptor.LoginInterceptor;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class AuthConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .order(HIGHEST_PRECEDENCE)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/users/login/**",
                        "/users/oauth/login/**",
                        "/users/join/**",
                        "/stores/**",
                        "/index.html",
                        "/error/**",
                        "/api/error/**",
                        "/docs/**",
                        "/nGrinder/**");


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
                        "/docs/**");
    }
}
