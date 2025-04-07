package upbrella.be.store.dto.response

import upbrella.be.umbrella.entity.Umbrella

data class CurrentUmbrellaStoreResponse(
    // TODO: 이거 id가 nullable 체크 필요.. (엔티티부터)
    val id: Long?,
    val name: String
) {
    companion object {
        fun fromUmbrella(umbrella: Umbrella): CurrentUmbrellaStoreResponse {
            return CurrentUmbrellaStoreResponse(
                id = umbrella.storeMeta.id,
                name = umbrella.storeMeta.name
            )
        }
    }
}
