package upbrella.be.store.dto.response

data class AllCurrentLocationStoreResponse(
    val stores: List<SingleCurrentLocationStoreResponse>
) {
    companion object {
        fun ofCreate(singleCurrentLocationStoreResponses: List<SingleCurrentLocationStoreResponse>) =
            AllCurrentLocationStoreResponse(singleCurrentLocationStoreResponses)
    }
}
