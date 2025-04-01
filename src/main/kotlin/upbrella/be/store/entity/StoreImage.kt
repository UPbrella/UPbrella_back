package upbrella.be.store.entity

import javax.persistence.*

@Entity
class StoreImage(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_detail_id")
    val storeDetail: StoreDetail? = null,
    var imageUrl: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    companion object {
        @JvmStatic
        fun createStoreImage(storeDetail: StoreDetail, imageUrl: String): StoreImage {
            return StoreImage(
                storeDetail = storeDetail,
                imageUrl = imageUrl,
            )
        }
    }
}