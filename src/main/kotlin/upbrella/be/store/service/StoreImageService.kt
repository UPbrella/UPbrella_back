package upbrella.be.store.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import upbrella.be.store.dto.response.AllImageUrlResponse
import upbrella.be.store.dto.response.SingleImageUrlResponse
import upbrella.be.store.entity.StoreDetail
import upbrella.be.store.entity.StoreImage
import upbrella.be.store.exception.NonExistingStoreImageException
import upbrella.be.store.repository.StoreImageRepository
import java.io.IOException
import java.util.UUID

@Service
class StoreImageService(
    private val s3Client: S3Client,
    private val storeImageRepository: StoreImageRepository,
    @Lazy private val storeDetailService: StoreDetailService
) {

    @Transactional
    fun uploadFile(file: MultipartFile, storeDetailId: Long, randomId: String): String {
        val fileName = file.originalFilename + randomId
        val contentType = file.contentType

        // Upload file
        val putObjectRequest = PutObjectRequest.builder()
            .bucket("bucketName")
            .key("store-image/$fileName")
            .acl("public-read")
            .contentDisposition("inline")
            .contentType(contentType)
            .build()

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.inputStream, file.size))
            val url = "https://file.upbrella.co.kr/store-image/$fileName"
            saveStoreImage(url, storeDetailId)
            return url
        } catch (e: IOException) {
            throw IllegalStateException("Failed to upload file", e)
        }
    }

    @Transactional
    fun deleteFile(imageId: Long) {
        val storeImage = storeImageRepository.findById(imageId)
            .orElseThrow { NonExistingStoreImageException("[ERROR] 해당 이미지가 존재하지 않습니다.") }
        storeImageRepository.deleteById(imageId)
        deleteFileInS3(storeImage.imageUrl!!)
    }

    fun createThumbnail(imageUrls: List<SingleImageUrlResponse>): String? {
        return imageUrls.firstOrNull()?.imageUrl
    }

    fun makeRandomId(): String {
        return UUID.randomUUID().toString().substring(0, 10)
    }

    @Transactional(readOnly = true)
    fun findAllImages(storeId: Long): AllImageUrlResponse {
        val storeDetail = storeDetailService.findByStoreMetaId(storeId)

        return AllImageUrlResponse.of(
            storeId,
            storeDetail.storeImages.map { SingleImageUrlResponse.createImageUrlResponse(it) }
        )
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
        val storeDetail = storeDetailService.findByStoreMetaId(storeId)
        storeImageRepository.save(StoreImage.createStoreImage(storeDetail, imageUrl))
    }

    private fun parseKey(url: String): String {
        val splitUrl = url.split("/")
        return splitUrl[splitUrl.size - 2] + "/" + splitUrl[splitUrl.size - 1]
    }
}