package upbrella.be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import upbrella.be.login.interceptor.OAuthLoginInterceptor;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        //TODO: CORS 세부 설정 예정
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(oAuthLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/oauth/**");
    }

    @Bean
    public OAuthLoginInterceptor oAuthLoginInterceptor() {
        return new OAuthLoginInterceptor();
    }


}