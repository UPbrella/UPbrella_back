package upbrella.be.config.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Configuration
@RequiredArgsConstructor
public class AuthTestConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .order(HIGHEST_PRECEDENCE)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/oauth/token",
                        "/user/me",
                        "/users/login/**",
                        "/users/oauth/login/**",
                        "/users/join/**",
                        "/stores/**",
                        "/index.html",
                        "/error/**",
                        "/api/error/**",
                        "/docs/**");


        registry.addInterceptor(adminInterceptor)
                .order(LOWEST_PRECEDENCE)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/oauth/token",
                        "/user/me",
                        "/users/login/**",
                        "/users/oauth/login/**",
                        "/users/join/**",
                        "/index.html",
                        "/error/**",
                        "/api/error/**",
                        "/docs/**");
    }
}
