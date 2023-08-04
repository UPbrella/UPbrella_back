package upbrella.be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import upbrella.be.config.interceptor.OAuthLoginInterceptor;

@Profile(value = "!dev")
@Configuration
public class AuthConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(oAuthLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/users/login/**",
                        "/users/join/**",
                        "/index.html",
                        "/error/**",
                        "/api/error/**",
                        "/docs/**");
    }

    @Bean
    public OAuthLoginInterceptor oAuthLoginInterceptor() {
        return new OAuthLoginInterceptor();
    }


}
