package upbrella.be.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "image.processing")
@ConstructorBinding
data class ImageProcessingConfig(
    val sizes: ImageSizes = ImageSizes(),
    val quality: ImageQuality = ImageQuality()
)

data class ImageSizes(
    val thumbnail: Int = 300,
    val medium: Int = 800,
    val large: Int = 1200
)

data class ImageQuality(
    val webp: Float = 0.85f,
    val jpeg: Float = 0.90f
)
