package upbrella.be.config

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector
import upbrella.be.rent.dto.response.HistoryInfoDto
import upbrella.be.rent.dto.response.RentalHistoryResponse
import upbrella.be.rent.entity.History
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
import upbrella.be.umbrella.dto.response.UmbrellaResponse
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.user.dto.token.OauthToken
import upbrella.be.user.entity.User
import java.time.LocalDateTime

object FixtureFactory {

    private val fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build()

    @JvmStatic
    fun buildStoreMetaWithId(id: Long): StoreMeta {
        return FixtureBuilderFactory.builderStoreMeta()
            .set("id", id)
            .sample()
    }

    @JvmStatic
    fun buildUmbrellaWithUmbrellaRequestAndStoreMeta(
        umbrellaCreateRequest: UmbrellaCreateRequest,
        storeMeta: StoreMeta
    ): Umbrella {
        return FixtureBuilderFactory.builderUmbrella()
            .set("storeMeta", storeMeta)
            .set("uuid", umbrellaCreateRequest.uuid)
            .set("rentable", umbrellaCreateRequest.isRentable)
            .sample()
    }

    @JvmStatic
    fun buildUmbrellaWithIdAndUmbrellaRequestAndStoreMeta(
        id: Long,
        umbrellaModifyRequest: UmbrellaModifyRequest,
        storeMeta: StoreMeta
    ): Umbrella {
        return FixtureBuilderFactory.builderUmbrella()
            .set("id", id)
            .set("storeMeta", storeMeta)
            .set("uuid", umbrellaModifyRequest.uuid)
            .set("rentable", umbrellaModifyRequest.isRentable)
            .sample()
    }

    @JvmStatic
    fun buildUmbrellaResponseWithUmbrellaAndStoreMeta(
        umbrella: UmbrellaWithHistory,
        storeMeta: StoreMeta
    ): UmbrellaResponse {
        return fixtureMonkey.giveMeBuilder(UmbrellaResponse::class.java)
            .set("id", umbrella.id)
            .set("storeMetaId", storeMeta.id)
            .set("uuid", umbrella.uuid)
            .set("rentable", umbrella.isRentable)
            .set("storeName", storeMeta.name)
            .set("historyId", umbrella.historyId)
            .set("etc", umbrella.etc)
            .sample()
    }

    @JvmStatic
    fun buildJoinRequestWithUser(user: User): JoinRequest {
        return fixtureMonkey.giveMeBuilder(JoinRequest::class.java)
            .set("name", user.name)
            .set("phoneNumber", user.phoneNumber)
            .sample()
    }

    @JvmStatic
    fun buildHistoryWithUmbrella(umbrella: Umbrella): History {
        return fixtureMonkey.giveMeBuilder(History::class.java)
            .set("umbrella", umbrella)
            .sample()
    }

    @JvmStatic
    fun buildOauthToken(): OauthToken {
        return fixtureMonkey.giveMeBuilder(OauthToken::class.java)
            .set("accessToken", "accessToken")
            .set("refreshToken", "refreshToken")
            .set("tokenType", "tokenType")
            .set("expiresIn", 1000L)
            .sample()
    }

    @JvmStatic
    fun buildKakaoLoginResponse(): KakaoLoginResponse {
        return fixtureMonkey.giveMeOne(KakaoLoginResponse::class.java)
    }

    @JvmStatic
    fun buildRentalHistoryResponseWithHistory(history: HistoryInfoDto): RentalHistoryResponse {
        return fixtureMonkey.giveMeBuilder(RentalHistoryResponse::class.java)
            .set("id", history.id)
            .set("name", history.name)
            .set("phoneNumber", history.phoneNumber)
            .set("rentStoreName", history.rentStoreName)
            .set("rentAt", history.rentAt)
            .set("elapsedDay", calElapsedDay(history))
            .set("paid", history.paidAt != null)
            .set("umbrellaUuid", history.umbrellaUuid)
            .set("returnStoreName", history.returnStoreName)
            .set("returnAt", history.returnAt)
            .set("totalRentalDay", history.returnAt.dayOfYear - history.rentAt.dayOfYear)
            .set("refundCompleted", true)
            .set("bank", history.bank)
            .set("accountNumber", history.accountNumber)
            .set("etc", history.etc)
            .sample()
    }

    private fun calElapsedDay(history: HistoryInfoDto): Int {
        var elapsedDay = LocalDateTime.now().dayOfYear - history.rentAt.dayOfYear
        if (history.returnAt != null) {
            elapsedDay = history.returnAt.dayOfYear - history.rentAt.dayOfYear
        }
        return elapsedDay
    }
}
