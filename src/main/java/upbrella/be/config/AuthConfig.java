package upbrella.be.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import upbrella.be.config.interceptor.AdminInterceptor;
import upbrella.be.config.interceptor.LoginInterceptor;
import upbrella.be.config.interceptor.OAuthLoginInterceptor;

@Profile(value = "!dev")
@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final OAuthLoginInterceptor oAuthLoginInterceptor;
    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(oAuthLoginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/users/login/**",
                        "/users/join/**",
                        "/index.html",
                        "/error/**",
                        "/api/error/**",
                        "/docs/**");

        registry.addInterceptor(loginInterceptor);

        registry.addInterceptor(adminInterceptor);
    }
}
