package upbrella.be.store.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import upbrella.be.store.dto.request.*
import upbrella.be.store.dto.response.*
import upbrella.be.store.service.*
import upbrella.be.util.CustomResponse

import java.time.LocalDateTime

@RestController
class StoreController(
    private val storeImageService: StoreImageService,
    private val storeMetaService: StoreMetaService,
    private val classificationService: ClassificationService,
    private val storeDetailService: StoreDetailService,
    private val businessHourService: BusinessHourService
) {

    @GetMapping("/stores/{storeId}")
    fun findStoreById(@PathVariable storeId: Long): ResponseEntity<CustomResponse<StoreFindByIdResponse>> {
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "가게 조회 성공",
                storeDetailService.findStoreDetailByStoreId(storeId)
            ))
    }

    @GetMapping("/stores/classification/{classificationId}")
    fun findCurrentLocationStore(@PathVariable classificationId: Long): ResponseEntity<CustomResponse<AllCurrentLocationStoreResponse>> {
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "대분류 기준 가게 조회 성공",
                storeMetaService.findAllStoresByClassification(classificationId, LocalDateTime.now())
            ))
    }

    @GetMapping("/stores/location/{umbrellaId}")
    fun findCurrentUmbrellaStore(@PathVariable umbrellaId: Long): ResponseEntity<CustomResponse<CurrentUmbrellaStoreResponse>> {
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "현재 우산 가게 조회 성공",
                storeMetaService.findCurrentStoreIdByUmbrella(umbrellaId)
            ))
    }

    @GetMapping("/admin/stores")
    fun findAllStores(): ResponseEntity<CustomResponse<AllStoreResponse>> {
        val allStores = storeDetailService.findAllStores()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "어드민 가게 전체 조회 성공",
                AllStoreResponse.builder()
                    .stores(allStores)
                    .build()
            ))
    }

    @PostMapping("/admin/stores")
    fun createStore(@RequestBody newStore: CreateStoreRequest): ResponseEntity<CustomResponse<Void>> {
        storeMetaService.createStore(newStore)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "새로운 협업지점 등록 성공"
            ))
    }

    @PatchMapping("/admin/stores/{storeId}")
    fun updateStore(
        @PathVariable storeId: Long,
        @RequestBody updateStore: UpdateStoreRequest
    ): ResponseEntity<CustomResponse<Void>> {
        storeDetailService.updateStore(storeId, updateStore)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "협업지점 정보 수정 성공"
            ))
    }

    @GetMapping("/admin/stores/{storeId}/images")
    fun findAllImages(@PathVariable storeId: Long): ResponseEntity<CustomResponse<AllImageUrlResponse>> {
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "협업지점 이미지 전체 조회 성공",
                storeImageService.findAllImages(storeId)
            ))
    }

    @PostMapping(value = ["/admin/stores/{storeId}/images"], consumes = ["multipart/form-data"])
    fun uploadStoreImage(
        @RequestPart image: MultipartFile,
        @PathVariable storeId: Long
    ): ResponseEntity<CustomResponse<Void>> {
        storeImageService.uploadFile(image, storeId, storeImageService.makeRandomId())

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "협업지점 이미지 업로드 성공"
            ))
    }

    @DeleteMapping("/admin/stores/images/{imageId}")
    fun deleteStoreImage(@PathVariable imageId: Long): ResponseEntity<CustomResponse<Void>> {
        storeImageService.deleteFile(imageId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "협업지점 이미지 삭제 성공"
            ))
    }

    @DeleteMapping("/admin/stores/{storeId}")
    fun deleteStore(@PathVariable storeId: Long): ResponseEntity<CustomResponse<Void>> {
        storeMetaService.deleteStoreMeta(storeId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "협업지점 삭제 성공"
            ))
    }

    @GetMapping("/stores/classifications")
    fun findAllClassification(): ResponseEntity<CustomResponse<AllClassificationResponse>> {
        val classifications = classificationService.findAllClassification()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "대분류 전체 조회 성공",
                classifications
            ))
    }

    @PostMapping("/admin/stores/classifications")
    fun createClassification(@RequestBody newClassification: CreateClassificationRequest): ResponseEntity<CustomResponse<Void>> {
        classificationService.createClassification(newClassification)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "새로운 대분류 등록 성공"
            ))
    }

    @DeleteMapping("/admin/stores/classifications/{classificationId}")
    fun deleteClassification(@PathVariable classificationId: Long): ResponseEntity<CustomResponse<Void>> {
        classificationService.deleteClassification(classificationId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "대분류 삭제 성공"
            ))
    }

    @GetMapping("/stores/subClassifications")
    fun findAllSubClassification(): ResponseEntity<CustomResponse<AllSubClassificationResponse>> {
        val subClassifications = classificationService.findAllSubClassification()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "소분류 전체 조회 성공",
                subClassifications
            ))
    }

    @PostMapping("/admin/stores/subClassifications")
    fun createSubClassification(@RequestBody newSubClassification: CreateSubClassificationRequest): ResponseEntity<CustomResponse<Void>> {
        classificationService.createSubClassification(newSubClassification)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "새로운 소분류 등록 성공"
            ))
    }

    @DeleteMapping("/admin/stores/subClassifications/{subClassificationId}")
    fun deleteSubClassification(@PathVariable subClassificationId: Long): ResponseEntity<CustomResponse<Void>> {
        classificationService.deleteSubClassification(subClassificationId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "소분류 삭제 성공"
            ))
    }

    @GetMapping("/stores/introductions")
    fun findAllStoreMeta(): ResponseEntity<CustomResponse<AllStoreIntroductionResponse>> {
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "협업지점 메타 전체 조회 성공",
                storeDetailService.findAllStoreIntroductions()
            ))
    }

    @PatchMapping("/admin/stores/{storeId}/activate")
    fun activateStoreStatus(@PathVariable storeId: Long): ResponseEntity<CustomResponse<Void>> {
        storeMetaService.activateStoreStatus(storeId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "협업지점 활성화 성공"
            ))
    }

    @PatchMapping("/admin/stores/{storeId}/inactivate")
    fun inActivateStoreStatus(@PathVariable storeId: Long): ResponseEntity<CustomResponse<Void>> {
        storeMetaService.inactivateStoreStatus(storeId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "협업지점 비활성화 성공"
            ))
    }

    @GetMapping("/admin/stores/{storeId}/businessHours")
    fun findAllBusinessHours(@PathVariable storeId: Long): ResponseEntity<CustomResponse<AllBusinessHourResponse>> {
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "협업지점 영업시간 전체 조회 성공",
                businessHourService.findAllBusinessHours(storeId)
            ))
    }
}
