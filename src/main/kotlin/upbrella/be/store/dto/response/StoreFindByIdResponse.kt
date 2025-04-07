package upbrella.be.store.dto.response

import upbrella.be.store.entity.StoreDetail
import java.time.LocalDateTime

data class StoreFindByIdResponse(
    // TODO: 엔티티부터 nullable 필드 확인 필요
    val id: Long?,
    val name: String?,
    val category: String?,
    val availableUmbrellaCount: Long,
    val openStatus: Boolean?,
    val businessHours: String?,
    val contactNumber: String?,
    val instaUrl: String?,
    val address: String?,
    val umbrellaLocation: String?,
    val description: String?,
    val latitude: Double?,
    val longitude: Double?,
    val imageUrls: List<String>
) {
    companion object {
        fun fromStoreDetail(storeDetail: StoreDetail, availableUmbrellaCount: Long): StoreFindByIdResponse {
            return StoreFindByIdResponse(
                id = storeDetail.storeMeta?.id,
                name = storeDetail.storeMeta?.name,
                category = storeDetail.storeMeta?.category,
                availableUmbrellaCount = availableUmbrellaCount,
                openStatus = storeDetail.storeMeta?.isOpenStore(LocalDateTime.now()),
                businessHours = storeDetail.workingHour,
                contactNumber = storeDetail.contactInfo,
                instaUrl = storeDetail.instaUrl,
                address = storeDetail.address,
                umbrellaLocation = storeDetail.umbrellaLocation,
                description = storeDetail.content,
                latitude = storeDetail.storeMeta?.latitude,
                longitude = storeDetail.storeMeta?.longitude,
                imageUrls = storeDetail.storeImages.map {
                    requireNotNull(it.imageUrl) {
                        "Image URL should not be null"
                    }
                }
            )
        }
    }
}
