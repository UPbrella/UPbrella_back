package upbrella.be.store.dto.response

data class SingleCurrentLocationStoreResponse(
    val id: Long?,
    val name: String,
    val openStatus: Boolean,
    val latitude: Double,
    val longitude: Double,
    val rentableUmbrellasCount: Long
) {
    companion object {
        fun fromStoreMeta(
            openStatus: Boolean,
            storeMetaWithUmbrellaCount: StoreMetaWithUmbrellaCount
        ): SingleCurrentLocationStoreResponse {
            return SingleCurrentLocationStoreResponse(
                id = storeMetaWithUmbrellaCount.storeMeta.id,
                name = storeMetaWithUmbrellaCount.storeMeta.name,
                openStatus = openStatus,
                latitude = storeMetaWithUmbrellaCount.storeMeta.latitude,
                longitude = storeMetaWithUmbrellaCount.storeMeta.longitude,
                rentableUmbrellasCount = storeMetaWithUmbrellaCount.rentableUmbrellasCount
            )
        }
    }
}
