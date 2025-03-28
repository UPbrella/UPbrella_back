package upbrella.be.config

import com.navercorp.fixturemonkey.ArbitraryBuilder
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector
import com.navercorp.fixturemonkey.api.introspector.CompositeArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import net.jqwik.api.Arbitraries
import upbrella.be.rent.entity.History
import upbrella.be.store.entity.BusinessHour
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
import upbrella.be.umbrella.dto.response.UmbrellaResponse
import upbrella.be.umbrella.dto.response.UmbrellaStatisticsResponse
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.request.UpdateBankAccountRequest
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.user.dto.response.SingleHistoryResponse
import upbrella.be.user.entity.User
import upbrella.be.util.AesEncryptor
import java.time.LocalDateTime


object FixtureBuilderFactory {

    val fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE )
        .plugin(KotlinPlugin())
        .defaultNotNull(true)
        .build()


    private val nameList = arrayOf(
        "사과", "바나나", "딸기", "수박", "복숭아", "감자", "토마토", "오렌지", "키위", "자두",
        "귤도라지", "고구마", "파인애플", "레몬", "라임", "무화과", "치즈", "감자", "배추", "미역"
    )
    private val cafeList = arrayOf(
        "투썸", "스타벅스", "이디야", "커피빈", "엔젤리너스", "할리스", "탐앤탐스",
        "커피마마", "커피에반하다", "커피나무"
    )
    private val bankList = arrayOf(
        "농협", "신한", "우리", "카카오뱅크", "하나", "기업", "케이뱅크", "SC제일", "경남",
        "광주", "대구", "부산", "전북", "제주", "수협", "새마을", "신협", "우체국",
        "전북", "제주", "수협", "새마을", "신협", "우체국"
    )
    private val addressList = arrayOf(
        "신촌", "홍대", "강남", "강북", "강서", "강동", "서초", "서대문", "마포",
        "종로", "용산", "성동", "성북", "중랑", "중구", "동대문", "동작", "관악"
    )
    private val etcList = arrayOf("파손", "분실", "폐기", "기타")

    @JvmStatic
    fun pickRandomString(names: Array<String>): String {
        return names[buildInteger(10000) % names.size]
    }

    @JvmStatic
    fun pickPhoneNumberString(): String {
        val part1 = Arbitraries.integers().between(1000, 9999).sample()
        val part2 = Arbitraries.integers().between(1000, 9999).sample()
        return "010-$part1-$part2"
    }

    @JvmStatic
    fun pickAccountNumberString(): String {
        val part1 = Arbitraries.integers().between(100, 999).sample()
        val part2 = Arbitraries.integers().between(100, 999).sample()
        val part3 = Arbitraries.integers().between(100000, 999999).sample()
        return "$part1$part2$part3"
    }

    @JvmStatic
    fun buildInteger(bound: Int): Int {
        return Arbitraries.integers().between(1, bound).sample()
    }

    @JvmStatic
    fun buildLong(bound: Int): Long {
        return Arbitraries.integers().between(1, bound).sample().toLong()
    }

    @JvmStatic
    fun buildDouble(): Double {
        return Arbitraries.doubles().between(1.0, 100.0).sample()
    }

    @JvmStatic
    fun builderStoreMeta(): ArbitraryBuilder<StoreMeta> {
        return fixtureMonkey.giveMeBuilder(StoreMeta::class.java)
            .set("classification", builderClassification().sample())
            .set("deleted", false)
            .set("id", buildLong(100))
            .set("name", pickRandomString(cafeList))
            .set("address", pickRandomString(addressList))
            .set("category", pickRandomString(addressList))
            .set("password", buildInteger(10000).toString())
            .set("latitude", buildDouble())
            .set("longitude", buildDouble())
    }

    @JvmStatic
    fun builderUmbrella(): ArbitraryBuilder<Umbrella> {
        return fixtureMonkey.giveMeBuilder(Umbrella::class.java)
            .set("id", buildLong(10000))
            .set("uuid", buildLong(1000))
            .set("storeMeta", builderStoreMeta().sample())
            .set("deleted", false)
            .set("etc", pickRandomString(nameList))
    }

    @JvmStatic
    fun builderUmbrellaWithHistory(): ArbitraryBuilder<UmbrellaWithHistory> {
        return fixtureMonkey.giveMeBuilder(UmbrellaWithHistory::class.java)
            .set("id", buildLong(10000))
            .set("uuid", buildLong(1000))
            .set("historyId", buildLong(1000))
            .set("storeMeta", builderStoreMeta().sample())
            .set("deleted", false)
            .set("etc", pickRandomString(nameList))
    }

    @JvmStatic
    fun builderUmbrellaResponses(): ArbitraryBuilder<UmbrellaResponse> {
        return fixtureMonkey.giveMeBuilder(UmbrellaResponse::class.java)
            .set("id", buildLong(10000))
            .set("storeMetaId", buildLong(100))
            .set("historyId", buildLong(100))
            .set("uuid", buildLong(100))
            .set("storeName", pickRandomString(cafeList))
            .set("etc", "etc")
    }

    @JvmStatic
    fun builderUmbrellaCreateRequest(): ArbitraryBuilder<UmbrellaCreateRequest> {
        return fixtureMonkey.giveMeBuilder(UmbrellaCreateRequest::class.java)
            .set("storeMetaId", buildLong(100))
            .set("uuid", buildLong(100))
            .set("etc", pickRandomString(nameList))
    }

    @JvmStatic
    fun builderUmbrellaModifyRequest(): ArbitraryBuilder<UmbrellaModifyRequest> {
        return fixtureMonkey.giveMeBuilder(UmbrellaModifyRequest::class.java)
            .set("storeMetaId", buildLong(100))
            .set("uuid", buildLong(100))
            .set("etc", pickRandomString(nameList))
    }

    @JvmStatic
    fun builderUser(aesEncryptor: AesEncryptor): ArbitraryBuilder<User> {
        return fixtureMonkey.giveMeBuilder(User::class.java)
            .set("id", buildLong(100))
            .set("socialId", buildLong(100000000).hashCode().toLong())
            .set("name", pickRandomString(nameList))
            .set("phoneNumber", pickPhoneNumberString())
            .set("email", "email@email.com")
            .set("bank", aesEncryptor.encrypt(pickRandomString(bankList)))
            .set("accountNumber", aesEncryptor.encrypt(pickAccountNumberString()))
    }

    @JvmStatic
    fun builderSingleHistoryResponse(): ArbitraryBuilder<SingleHistoryResponse> {
        return fixtureMonkey.giveMeBuilder(SingleHistoryResponse::class.java)
            .set("umbrellaUuid", buildLong(1000))
            .set("rentedStore", pickRandomString(cafeList))
    }

    @JvmStatic
    fun builderJoinRequest(): ArbitraryBuilder<JoinRequest> {
        return fixtureMonkey.giveMeBuilder(JoinRequest::class.java)
            .set("name", pickRandomString(nameList))
            .set("phoneNumber", pickPhoneNumberString())
            .set("bank", pickRandomString(bankList))
            .set("accountNumber", pickAccountNumberString())
    }

    @JvmStatic
    fun builderBankAccount(): ArbitraryBuilder<UpdateBankAccountRequest> {
        return fixtureMonkey.giveMeBuilder(UpdateBankAccountRequest::class.java)
            .set("bank", pickRandomString(bankList))
            .set("accountNumber", "123456789")
    }

    @JvmStatic
    fun builderUmbrellaStatisticsResponse(): ArbitraryBuilder<UmbrellaStatisticsResponse> {
        val missingUmbrellaCount = buildInteger(100)
        val rentableUmbrellaCount = buildInteger(100)
        val rentedUmbrellaCount = buildInteger(100)
        val totalUmbrellaCount = missingUmbrellaCount + rentableUmbrellaCount + rentedUmbrellaCount
        val missingRate = 100.0 * missingUmbrellaCount / totalUmbrellaCount

        return fixtureMonkey.giveMeBuilder(UmbrellaStatisticsResponse::class.java)
            .set("totalUmbrellaCount", totalUmbrellaCount)
            .set("rentableUmbrellaCount", rentableUmbrellaCount)
            .set("rentedUmbrellaCount", rentedUmbrellaCount)
            .set("missingUmbrellaCount", missingUmbrellaCount)
            .set("totalRentCount", buildLong(1000))
            .set("missingRate", missingRate)
    }

    @JvmStatic
    fun builderClassification(): ArbitraryBuilder<Classification> {
        return fixtureMonkey.giveMeBuilder(Classification::class.java)
            .set("id", buildLong(100))
            .set("name", pickRandomString(nameList))
            .set("latitude", buildDouble())
            .set("longitude", buildDouble())
    }

    @JvmStatic
    fun builderBusinessHour(): ArbitraryBuilder<BusinessHour> {
        return fixtureMonkey.giveMeBuilder(BusinessHour::class.java)
            .set("id", buildLong(100))
    }

    @JvmStatic
    fun builderSessionUser(): ArbitraryBuilder<SessionUser> {
        return fixtureMonkey.giveMeBuilder(SessionUser::class.java)
            .set("id", buildLong(100))
            .set("socialId", buildLong(100))
            .set("adminStatus", false)
            .set("name", pickRandomString(nameList))
            .set("phoneNumber", pickPhoneNumberString())
    }

    @JvmStatic
    fun builderHistory(aesEncryptor: AesEncryptor): ArbitraryBuilder<History> {
        return fixtureMonkey.giveMeBuilder(History::class.java)
            .set("id", buildLong(100))
            .set("user", builderUser(aesEncryptor).sample())
            .set("umbrella", builderUmbrella().sample())
            .set("rentStoreMeta", builderStoreMeta().sample())
            .set("rentedAt", LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .set("returnedAt", LocalDateTime.of(2021, 1, 14, 0, 0, 0))
            .set("returnStoreMeta", builderStoreMeta().sample())
            .set("paidAt", LocalDateTime.of(2021, 1, 2, 0, 0, 0))
            .set("refundedAt", LocalDateTime.of(2021, 1, 15, 0, 0, 0))
            .set("refundedBy", builderUser(aesEncryptor).sample())
            .set("paidBy", builderUser(aesEncryptor).sample())
            .set("bank", pickRandomString(bankList))
            .set("accountNumber", pickRandomString(bankList))
            .set("etc", pickRandomString(etcList))
    }
}
