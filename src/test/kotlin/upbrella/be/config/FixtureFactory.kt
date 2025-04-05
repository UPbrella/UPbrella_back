package upbrella.be.config

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.instantiator.instantiateBy
import upbrella.be.rent.dto.response.HistoryInfoDto
import upbrella.be.rent.dto.response.RentalHistoryResponse
import upbrella.be.rent.entity.History
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.response.UmbrellaResponse
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.request.KakaoAccount
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.user.dto.token.OauthToken
import upbrella.be.user.entity.User
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object FixtureFactory {

    private val fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
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
            .set("rentable", umbrellaCreateRequest.rentable)
            .sample()
    }

    @JvmStatic
    fun buildUmbrellaResponseWithUmbrellaAndStoreMeta(
        umbrella: UmbrellaWithHistory,
        storeMeta: StoreMeta
    ): UmbrellaResponse {
        return fixtureMonkey.giveMeBuilder<UmbrellaResponse>()
            .instantiateBy {
                constructor()
            }
            .set("id", umbrella.id)
            .set("storeMetaId", storeMeta.id)
            .set("uuid", umbrella.uuid)
            .set("rentable", umbrella.rentable)
            .set("storeName", storeMeta.name)
            .set("historyId", umbrella.historyId)
            .set("etc", umbrella.etc)
            .sample()
    }

    @JvmStatic
    fun buildJoinRequestWithUser(user: User): JoinRequest {
        return fixtureMonkey.giveMeBuilder<JoinRequest>()
            .instantiateBy {
                constructor()
            }
            .set("name", user.name)
            .set("phoneNumber", user.phoneNumber)
            .sample()
    }

    @JvmStatic
    fun buildHistoryWithUmbrella(umbrella: Umbrella): History {
        return fixtureMonkey.giveMeBuilder<History>()
            .set("umbrella", umbrella)
            .sample()
    }

    @JvmStatic
    fun buildOauthToken(): OauthToken {
        return fixtureMonkey.giveMeBuilder<OauthToken>()
            .instantiateBy {
                constructor()
            }
            .set("accessToken", "accessToken")
            .set("refreshToken", "refreshToken")
            .set("tokenType", "tokenType")
            .set("expiresIn", 1000L)
            .sample()
    }

    @JvmStatic
    fun buildKakaoLoginResponse(): KakaoLoginResponse {
        return KakaoLoginResponse(
            id = 1L,
            kakaoAccount = KakaoAccount(email = "kakao@gmail.com")
        )
    }

    @JvmStatic
    fun buildRentalHistoryResponseWithHistory(history: HistoryInfoDto): RentalHistoryResponse {
        return fixtureMonkey.giveMeBuilder<RentalHistoryResponse>()
            .instantiateBy {
                constructor()
            }
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
            .set("totalRentalDay", history.returnAt?.let { it.dayOfYear - history.rentAt.dayOfYear } ?: 0)
            .set("refundCompleted", true)
            .set("bank", history.bank)
            .set("accountNumber", history.accountNumber)
            .set("etc", history.etc)
            .sample()
    }

    private fun calElapsedDay(history: HistoryInfoDto): Int {
        var elapsedDay : Int = ChronoUnit.DAYS.between(
            history.rentAt.toLocalDate(),
            LocalDateTime.now().toLocalDate()
        ).toInt()
        if (history.returnAt != null) {
            elapsedDay = ChronoUnit.DAYS.between(
                history.rentAt.toLocalDate(),
                history.returnAt?.toLocalDate() ?: LocalDateTime.now()
            ).toInt()
        }
        return elapsedDay
    }
}
