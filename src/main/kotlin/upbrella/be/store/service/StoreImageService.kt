package upbrella.be.store.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import upbrella.be.store.dto.response.AllImageUrlResponse
import upbrella.be.store.dto.response.ImageSizeUrls
import upbrella.be.store.dto.response.ImageUrlsResponse
import upbrella.be.store.dto.response.SingleImageUrlResponse
import upbrella.be.store.entity.StoreImage
import upbrella.be.store.exception.NonExistingStoreImageException
import upbrella.be.store.repository.StoreDetailReader
import upbrella.be.store.repository.StoreImageReader
import upbrella.be.store.repository.StoreImageWriter
import java.io.IOException
import java.util.*

@Service
class StoreImageService(
    private val s3Client: S3Client,
    private val storeImageReader: StoreImageReader,
    private val storeImageWriter: StoreImageWriter,
    private val storeDetailReader: StoreDetailReader,
    private val imageProcessingService: ImageProcessingService,
) {

    @Transactional
    fun uploadFile(file: MultipartFile, storeDetailId: Long, randomId: String): String {
        try {
            // 1. 이미지 처리 (3가지 크기 x 2가지 포맷 = 6개 파일)
            val processed = imageProcessingService.processImage(file)

            // 2. S3에 6개 파일 업로드
            val imageUrlsResponse = uploadAllVariantsToS3(processed, storeDetailId, randomId)

            // 3. DB에 저장
            saveStoreImageWithUrls(imageUrlsResponse, storeDetailId)

            // 4. 기존 호환성: medium JPEG URL 반환
            return imageUrlsResponse.jpeg.medium
        } catch (e: Exception) {
            throw IllegalStateException("Failed to upload file", e)
        }
    }

    @Transactional
    fun deleteFile(imageId: Long) {
        val storeImage = storeImageReader.findById(imageId)
            ?: throw NonExistingStoreImageException("[ERROR] 해당 이미지가 존재하지 않습니다.")

        // 다중 URL 구조면 모든 variant 삭제, 기존 구조면 단일 URL만 삭제
        val imageUrlsResponse = storeImage.getImageUrlsResponse()

        // WebP 파일 삭제
        imageUrlsResponse.webp?.let {
            deleteFileInS3(it.thumb)
            deleteFileInS3(it.medium)
            deleteFileInS3(it.large)
        }

        // JPEG 파일 삭제
        deleteFileInS3(imageUrlsResponse.jpeg.thumb)
        deleteFileInS3(imageUrlsResponse.jpeg.medium)
        deleteFileInS3(imageUrlsResponse.jpeg.large)

        storeImageWriter.deleteById(imageId)
    }

    fun createThumbnail(imageUrls: List<SingleImageUrlResponse>): String? {
        // WebP 썸네일 우선, 없으면 JPEG 썸네일 반환
        return imageUrls.firstOrNull()?.let { firstImage ->
            firstImage.imageUrls.webp?.thumb ?: firstImage.imageUrls.jpeg.thumb
        }
    }

    fun makeRandomId(): String {
        return UUID.randomUUID().toString().substring(0, 10)
    }

    @Transactional(readOnly = true)
    fun findAllImages(storeId: Long): AllImageUrlResponse {
        val storeDetail = storeDetailReader.findByStoreMetaId(storeId)

        return AllImageUrlResponse.of(
            storeId,
            storeDetail.storeImages.map { SingleImageUrlResponse.createImageUrlResponse(it) }
        )
    }

    /**
     * 6개 파일(3 크기 x 2 포맷)을 S3에 업로드
     */
    private fun uploadAllVariantsToS3(
        processed: ProcessedImageSet,
        storeDetailId: Long,
        randomId: String
    ): ImageUrlsResponse {
        val baseKey = "store-image/$storeDetailId/$randomId"
        val uploadedKeys = mutableListOf<String>()

        try {
            // 6개 파일 업로드
            val thumbWebp = uploadToS3("${baseKey}_thumb.webp", processed.thumbnail.webp, "image/webp")
                .also { uploadedKeys.add(it) }
            val thumbJpeg = uploadToS3("${baseKey}_thumb.jpg", processed.thumbnail.jpeg, "image/jpeg")
                .also { uploadedKeys.add(it) }
            val mediumWebp = uploadToS3("${baseKey}_medium.webp", processed.medium.webp, "image/webp")
                .also { uploadedKeys.add(it) }
            val mediumJpeg = uploadToS3("${baseKey}_medium.jpg", processed.medium.jpeg, "image/jpeg")
                .also { uploadedKeys.add(it) }
            val largeWebp = uploadToS3("${baseKey}_large.webp", processed.large.webp, "image/webp")
                .also { uploadedKeys.add(it) }
            val largeJpeg = uploadToS3("${baseKey}_large.jpg", processed.large.jpeg, "image/jpeg")
                .also { uploadedKeys.add(it) }

            return ImageUrlsResponse(
                id = null,
                webp = ImageSizeUrls(thumbWebp, mediumWebp, largeWebp),
                jpeg = ImageSizeUrls(thumbJpeg, mediumJpeg, largeJpeg)
            )
        } catch (e: Exception) {
            // 업로드 실패 시 이미 업로드된 파일들 삭제 (rollback)
            uploadedKeys.forEach { url ->
                try {
                    deleteFileInS3(url)
                } catch (deleteError: Exception) {
                    // 로그만 남기고 계속 진행
                }
            }
            throw e
        }
    }

    /**
     * 단일 파일을 S3에 업로드하고 URL 반환
     */
    private fun uploadToS3(key: String, data: ByteArray, contentType: String): String {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket("bucketName")
            .key(key)
            .acl("public-read")
            .contentDisposition("inline")
            .contentType(contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data))
        return "https://file.upbrella.co.kr/$key"
    }

    private fun deleteFileInS3(imgUrl: String) {
        val key = parseKey(imgUrl)

        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket("bucketName")
            .key(key)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }

    private fun saveStoreImage(imageUrl: String, storeId: Long) {
        val storeDetail = storeDetailReader.findByStoreMetaId(storeId)
        storeImageWriter.save(StoreImage.createStoreImage(storeDetail, imageUrl))
    }

    private fun saveStoreImageWithUrls(imageUrlsResponse: ImageUrlsResponse, storeId: Long) {
        val storeDetail = storeDetailReader.findByStoreMetaId(storeId)
        storeImageWriter.save(StoreImage.createStoreImageWithUrls(storeDetail, imageUrlsResponse))
    }

    private fun parseKey(url: String): String {
        val splitUrl = url.split("/")
        return if (splitUrl.size >= 2) {
            splitUrl[splitUrl.size - 3] + "/" + splitUrl[splitUrl.size - 2] + "/" + splitUrl[splitUrl.size - 1]
        } else {
            splitUrl[splitUrl.size - 2] + "/" + splitUrl[splitUrl.size - 1]
        }
    }
}
