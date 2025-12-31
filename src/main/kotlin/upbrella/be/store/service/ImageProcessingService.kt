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
        try {
            // Thumbnailator는 EXIF orientation을 자동으로 처리
            // 먼저 안전한 크기로 다운샘플링 (메모리 절약)
            val inputStream = file.inputStream
            val originalImage = ImageIO.read(inputStream)
                ?: throw IllegalArgumentException("Invalid image file")

            try {
                // 이미지가 너무 크면 먼저 다운샘플링 (메모리 절약)
                val workingImage = if (originalImage.width > 2000 || originalImage.height > 2000) {
                    // Thumbnailator는 자동으로 EXIF orientation 적용
                    Thumbnails.of(originalImage)
                        .size(2000, 2000)
                        .asBufferedImage()
                        .also { originalImage.flush() }
                } else {
                    originalImage
                }

                try {
                    // 3가지 크기로 리사이징 (순차 처리)
                    val thumbnail = processSize(workingImage, config.sizes.thumbnail)
                    val medium = processSize(workingImage, config.sizes.medium)
                    val large = processSize(workingImage, config.sizes.large)

                    return ProcessedImageSet(thumbnail, medium, large)
                } finally {
                    // 작업 이미지 메모리 해제
                    workingImage.flush()
                }
            } catch (e: OutOfMemoryError) {
                throw IllegalStateException("Image is too large to process. Please use a smaller image (max 10MB).", e)
            }
        } catch (e: OutOfMemoryError) {
            throw IllegalStateException("Image is too large to process. Please use a smaller image (max 10MB).", e)
        }
    }

    /**
     * 특정 크기로 리사이징하고 WebP와 JPEG 포맷으로 변환
     */
    private fun processSize(originalImage: BufferedImage, targetSize: Int): ImageVariantData {
        // 리사이징
        val resized = resizeImage(originalImage, targetSize)

        try {
            // WebP 변환
            val webp = convertToWebP(resized, config.quality.webp)

            // JPEG 변환
            val jpeg = convertToJpeg(resized, config.quality.jpeg)

            return ImageVariantData(webp, jpeg)
        } finally {
            // 리사이즈된 이미지 메모리 해제
            resized.flush()
        }
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
        ByteArrayOutputStream().use { outputStream ->
            val writer = ImageIO.getImageWritersByFormatName("webp").next()
                ?: throw IllegalStateException("WebP ImageWriter not found. Make sure webp-imageio is on the classpath")

            try {
                val writeParam = writer.defaultWriteParam
                if (writeParam.canWriteCompressed()) {
                    writeParam.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
                    // WebP는 compression type을 먼저 설정해야 함
                    writeParam.compressionType = writeParam.compressionTypes?.firstOrNull() ?: "Lossy"
                    writeParam.compressionQuality = quality
                }

                ImageIO.createImageOutputStream(outputStream).use { output ->
                    writer.output = output
                    writer.write(null, javax.imageio.IIOImage(image, null, null), writeParam)
                }

                return outputStream.toByteArray()
            } finally {
                writer.dispose()
            }
        }
    }

    /**
     * BufferedImage를 JPEG 포맷으로 변환
     */
    private fun convertToJpeg(image: BufferedImage, quality: Float): ByteArray {
        ByteArrayOutputStream().use { outputStream ->
            // RGB 이미지로 변환 (JPEG는 투명도 미지원)
            val needsConversion = image.type == BufferedImage.TYPE_INT_ARGB ||
                                  image.type == BufferedImage.TYPE_4BYTE_ABGR

            val rgbImage = if (needsConversion) {
                BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB).apply {
                    val g = createGraphics()
                    try {
                        g.drawImage(image, 0, 0, null)
                    } finally {
                        g.dispose()
                    }
                }
            } else {
                image
            }

            try {
                val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
                try {
                    val writeParam = writer.defaultWriteParam
                    writeParam.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
                    writeParam.compressionQuality = quality

                    ImageIO.createImageOutputStream(outputStream).use { output ->
                        writer.output = output
                        writer.write(null, javax.imageio.IIOImage(rgbImage, null, null), writeParam)
                    }

                    return outputStream.toByteArray()
                } finally {
                    writer.dispose()
                }
            } finally {
                // 새로 생성한 RGB 이미지는 메모리 해제
                if (needsConversion) {
                    rgbImage.flush()
                }
            }
        }
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
