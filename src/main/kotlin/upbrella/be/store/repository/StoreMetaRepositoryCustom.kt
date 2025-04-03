package upbrella.be.store.repository

import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount

interface StoreMetaRepositoryCustom {
    fun findAllStoresByClassification(classificationId: Long): List<StoreMetaWithUmbrellaCount>
}