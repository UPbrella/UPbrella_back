package upbrella.be.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Path

@Configuration
class UploadConfig(@Value("\${upbrella.uploads-directory:./uploads}") private val directory: String) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(Path.of(directory).toAbsolutePath().normalize().toUri().toString().trimEnd('/') + "/")
            .setCachePeriod(3600)
    }
}
