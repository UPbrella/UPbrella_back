package upbrella.be.store.entity

import upbrella.be.store.dto.request.CreateStoreRequest
import upbrella.be.store.dto.request.UpdateStoreRequest
import javax.persistence.*

@Entity
class StoreDetail(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_meta_id")
    var storeMeta: StoreMeta? = null,
    var umbrellaLocation: String? = null,
    var workingHour: String? = null,
    var instaUrl: String? = null,
    var contactInfo: String? = null,
    var address: String? = null,
    var addressDetail: String? = null,
    var content: String? = null,
    @OneToMany(mappedBy= "storeDetail", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val storeImages: List<StoreImage> = emptyList(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    companion object {
        @JvmStatic
        fun createForSave(request: CreateStoreRequest, storeMeta: StoreMeta): StoreDetail {
            return StoreDetail(
                storeMeta = storeMeta,
                umbrellaLocation = request.umbrellaLocation,
                workingHour = request.businessHour,
                instaUrl = request.instagramId,
                contactInfo = request.contactNumber,
                address = request.address,
                addressDetail = request.addressDetail,
                content = request.content,
            )
        }
    }

    fun updateStore(storeMeta: StoreMeta, request: UpdateStoreRequest) {
        this.storeMeta = storeMeta
        this.umbrellaLocation = request.umbrellaLocation
        this.workingHour = request.businessHour
        this.instaUrl = request.instagramId
        this.contactInfo = request.contactNumber
        this.address = request.address
        this.addressDetail = request.addressDetail
        this.content = request.content
    }

    fun getSortedStoreImages(): List<StoreImage> {
        return storeImages.stream()
            .sorted { o1, o2 -> (o1.id - o2.id).toInt() }
            .toList()
    }
}