package upbrella.be.store.service

import net.coobird.thumbnailator.Thumbnails
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import upbrella.be.config.ImageProcessingConfig
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class ImageProcessingService(
    private val config: ImageProcessingConfig
) {

    enum class ImageSize(val maxSize: Int) {
        THUMBNAIL(300),
        MEDIUM(800),
        LARGE(1200);

        companion object {
            fun from(configValue: Int): ImageSize {
                return when (configValue) {
                    300 -> THUMBNAIL
                    800 -> MEDIUM
                    1200 -> LARGE
                    else -> throw IllegalArgumentException("Unknown image size: $configValue")
                }
            }
        }
    }

    /**
     * 이미지를 3가지 크기 x 2가지 포맷(WebP, JPEG)으로 처리
     */
    fun processImage(file: MultipartFile): ProcessedImageSet {
        val originalImage = ImageIO.read(file.inputStream)
            ?: throw IllegalArgumentException("Invalid image file")

        // 3가지 크기로 리사이징
        val thumbnail = processSize(originalImage, config.sizes.thumbnail)
        val medium = processSize(originalImage, config.sizes.medium)
        val large = processSize(originalImage, config.sizes.large)

        return ProcessedImageSet(thumbnail, medium, large)
    }

    /**
     * 특정 크기로 리사이징하고 WebP와 JPEG 포맷으로 변환
     */
    private fun processSize(originalImage: BufferedImage, targetSize: Int): ImageVariantData {
        // 리사이징
        val resized = resizeImage(originalImage, targetSize)

        // WebP 변환
        val webp = convertToWebP(resized, config.quality.webp)

        // JPEG 변환
        val jpeg = convertToJpeg(resized, config.quality.jpeg)

        return ImageVariantData(webp, jpeg)
    }

    /**
     * 이미지를 지정된 크기로 리사이징 (비율 유지)
     */
    private fun resizeImage(image: BufferedImage, maxSize: Int): BufferedImage {
        return Thumbnails.of(image)
            .size(maxSize, maxSize)
            .asBufferedImage()
    }

    /**
     * BufferedImage를 WebP 포맷으로 변환
     */
    private fun convertToWebP(image: BufferedImage, quality: Float): ByteArray {
        val outputStream = ByteArrayOutputStream()

        // WebP ImageWriter 설정
        val writer = ImageIO.getImageWritersByFormatName("webp").next()
            ?: throw IllegalStateException("WebP ImageWriter not found. Make sure webp-imageio is on the classpath")

        val writeParam = writer.defaultWriteParam
        if (writeParam.canWriteCompressed()) {
            writeParam.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
            writeParam.compressionQuality = quality
        }

        val output = ImageIO.createImageOutputStream(outputStream)
        writer.output = output
        writer.write(null, javax.imageio.IIOImage(image, null, null), writeParam)
        writer.dispose()
        output.close()

        return outputStream.toByteArray()
    }

    /**
     * BufferedImage를 JPEG 포맷으로 변환
     */
    private fun convertToJpeg(image: BufferedImage, quality: Float): ByteArray {
        val outputStream = ByteArrayOutputStream()

        // RGB 이미지로 변환 (JPEG는 투명도 미지원)
        val rgbImage = if (image.type == BufferedImage.TYPE_INT_ARGB ||
                           image.type == BufferedImage.TYPE_4BYTE_ABGR) {
            val newImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
            val g = newImage.createGraphics()
            g.drawImage(image, 0, 0, null)
            g.dispose()
            newImage
        } else {
            image
        }

        val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
        val writeParam = writer.defaultWriteParam
        writeParam.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
        writeParam.compressionQuality = quality

        val output = ImageIO.createImageOutputStream(outputStream)
        writer.output = output
        writer.write(null, javax.imageio.IIOImage(rgbImage, null, null), writeParam)
        writer.dispose()
        output.close()

        return outputStream.toByteArray()
    }
}

/**
 * 3가지 크기의 이미지 데이터 세트
 */
data class ProcessedImageSet(
    val thumbnail: ImageVariantData,
    val medium: ImageVariantData,
    val large: ImageVariantData
)

/**
 * 각 크기별 WebP와 JPEG 데이터
 */
data class ImageVariantData(
    val webp: ByteArray,
    val jpeg: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageVariantData

        if (!webp.contentEquals(other.webp)) return false
        if (!jpeg.contentEquals(other.jpeg)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = webp.contentHashCode()
        result = 31 * result + jpeg.contentHashCode()
        return result
    }
}
