package upbrella.be.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
        val objectMapper = builder.build<ObjectMapper>()
        objectMapper.registerModule(JavaTimeModule()) // Register module to support Java 8's java.time
        return objectMapper
    }
}
