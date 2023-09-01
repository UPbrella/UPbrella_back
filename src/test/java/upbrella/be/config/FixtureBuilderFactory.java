package upbrella.be.config;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.random.Randoms;
import net.jqwik.api.Arbitraries;
import upbrella.be.rent.dto.response.RentalHistoryResponse;
import upbrella.be.rent.entity.History;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest;
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.dto.response.UmbrellaStatisticsResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.request.UpdateBankAccountRequest;
import upbrella.be.user.dto.response.SessionUser;
import upbrella.be.user.dto.response.SingleHistoryResponse;
import upbrella.be.user.entity.User;

import java.time.LocalDateTime;

public class FixtureBuilderFactory {

    private static final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .defaultNotNull(true)
            .build();

    private static String[] nameList = {"사과", "바나나", "딸기", "수박", "복숭아", "감자", "토마토", "오렌지", "키위", "자두", "귤도라지", "고구마", "파인애플", "레몬", "라임", "무화과", "치즈", "감자", "배추", "미역"};
    private static String[] cafeList = {"투썸", "스타벅스", "이디야", "커피빈", "엔젤리너스", "할리스", "탐앤탐스", "커피마마", "커피에반하다", "커피나무"};
    private static String[] bankList = {"농협", "신한", "우리", "카카오뱅크", "하나", "기업", "케이뱅크", "SC제일", "경남", "광주", "대구", "부산", "전북", "제주", "수협", "새마을", "신협", "우체국", "전북", "제주", "수협", "새마을", "신협", "우체국"};
    private static String[] addressList = {"신촌", "홍대", "강남", "강북", "강서", "강동", "서초", "서대문", "마포", "종로", "용산", "성동", "성북", "중랑", "중구", "동대문", "동작", "관악"};
    private static String[] etcList = {"파손", "분실", "폐기", "기타"};

    private static String pickRandomString(String[] names) {

        return names[buildInteger(10000) % names.length];
    }

    private static String pickPhoneNumberString() {

        StringBuilder sb = new StringBuilder();
        sb.append("010")
                .append("-")
                .append(Arbitraries.integers().between(1000, 9999).sample())
                .append("-")
                .append(Arbitraries.integers().between(1000, 9999).sample());

        return sb.toString();
    }

    private static String pickAccountNumberString() {

        StringBuilder sb = new StringBuilder();
        sb.append(Arbitraries.integers().between(100, 999).sample())
                .append("-")
                .append(Arbitraries.integers().between(100, 999).sample())
                .append("-")
                .append(Arbitraries.integers().between(100000, 999999).sample());

        return sb.toString();
    }

    public static int buildInteger(int bound) {

        return Randoms.nextInt(bound);
    }

    public static long buildLong(int bound) {

        return Randoms.nextInt(bound);
    }

    public static double buildDouble() {

        return Arbitraries.doubles().between(1, 100).sample();
    }

    public static ArbitraryBuilder<StoreMeta> builderStoreMeta() {

        return fixtureMonkey.giveMeBuilder(StoreMeta.class)
                .set("deleted", false)
                .set("id", buildLong(100))
                .set("name", pickRandomString(cafeList))
                .set("address", pickRandomString(addressList))
                .set("category", pickRandomString(addressList))
                .set("password", String.valueOf(buildInteger(10000)))
                .set("latitude", buildDouble())
                .set("longitude", buildDouble());
    }

    public static ArbitraryBuilder<Umbrella> builderUmbrella() {

        return fixtureMonkey.giveMeBuilder(Umbrella.class)
                .set("id", buildLong(10000))
                .set("uuid", buildLong(1000))
                .set("storeMeta", builderStoreMeta().sample())
                .set("deleted", false)
                .set("etc", pickRandomString(nameList));
    }

    public static ArbitraryBuilder<UmbrellaResponse> builderUmbrellaResponses() {

        return fixtureMonkey.giveMeBuilder(UmbrellaResponse.class)
                .set("id", buildLong(10000))
                .set("storeMetaId", buildLong(100))
                .set("uuid", buildLong(100));
    }

    public static ArbitraryBuilder<UmbrellaCreateRequest> builderUmbrellaCreateRequest() {

        return fixtureMonkey.giveMeBuilder(UmbrellaCreateRequest.class)
                .set("storeMetaId", buildLong(100))
                .set("uuid", buildLong(100))
                .set("etc", pickRandomString(nameList));
    }


    public static ArbitraryBuilder<UmbrellaModifyRequest> builderUmbrellaModifyRequest() {

        return fixtureMonkey.giveMeBuilder(UmbrellaModifyRequest.class)
                .set("storeMetaId", buildLong(100))
                .set("uuid", buildLong(100))
                .set("etc", pickRandomString(nameList));
    }

    public static ArbitraryBuilder<User> builderUser() {

        return fixtureMonkey.giveMeBuilder(User.class)
                .set("id", buildLong(100))
                .set("socialId", buildLong(100000000))
                .set("name", pickRandomString(nameList))
                .set("phoneNumber", pickPhoneNumberString())
                .set("bank", pickRandomString(bankList))
                .set("accountNumber", pickAccountNumberString());
    }


    public static ArbitraryBuilder<SingleHistoryResponse> builderSingleHistoryResponse() {

        return fixtureMonkey.giveMeBuilder(SingleHistoryResponse.class)
                .set("umbrellaUuid", buildLong(1000))
                .set("rentedStore", pickRandomString(cafeList));
    }

    public static ArbitraryBuilder<JoinRequest> builderJoinRequest() {

        return fixtureMonkey.giveMeBuilder(JoinRequest.class)
                .set("name", pickRandomString(nameList))
                .set("phoneNumber", pickPhoneNumberString())
                .set("bank", pickRandomString(bankList))
                .set("accountNumber", pickAccountNumberString());
    }

    public static ArbitraryBuilder<UpdateBankAccountRequest> builderBankAccount() {

        return fixtureMonkey.giveMeBuilder(UpdateBankAccountRequest.class)
                .set("bank", pickRandomString(bankList))
                .set("accountNumber", pickAccountNumberString());
    }

    public static ArbitraryBuilder<UmbrellaStatisticsResponse> builderUmbrellaStatisticsResponse() {

        int missingUmbrellaCount = buildInteger(100);
        int rentableUmbrellaCount = buildInteger(100);
        int rentedUmbrellaCount = buildInteger(100);
        int totalUmbrellaCount = missingUmbrellaCount + rentableUmbrellaCount + rentedUmbrellaCount;
        double missingRate = (double) 100 * missingUmbrellaCount / totalUmbrellaCount;
        return fixtureMonkey.giveMeBuilder(UmbrellaStatisticsResponse.class)
                .set("totalUmbrellaCount", totalUmbrellaCount)
                .set("rentableUmbrellaCount", buildInteger(100))
                .set("rentedUmbrellaCount", buildInteger(100))
                .set("missingUmbrellaCount", missingUmbrellaCount)
                .set("totalRentCount", buildLong(1000))
                .set("missingRate", missingRate);
    }

    public static ArbitraryBuilder<Classification> builderClassification() {

        return fixtureMonkey.giveMeBuilder(Classification.class)
                .set("id", buildLong(100))
                .set("name", pickRandomString(nameList))
                .set("latitude", buildDouble())
                .set("longitude", buildDouble());
    }

    public static ArbitraryBuilder<BusinessHour> builderBusinessHour() {

        return fixtureMonkey.giveMeBuilder(BusinessHour.class)
                .set("id", buildLong(100));
    }

    public static ArbitraryBuilder<SessionUser> builderSessionUser() {

        return fixtureMonkey.giveMeBuilder(SessionUser.class)
                .set("id", buildLong(100))
                .set("socialId", buildLong(100))
                .set("adminStatus", false)
                .set("name", pickRandomString(nameList))
                .set("phoneNumber", pickPhoneNumberString());
    }

    public static ArbitraryBuilder<History> builderHistory() {

        return fixtureMonkey.giveMeBuilder(History.class)
                .set("id", buildLong(100))
                .set("user", builderUser().sample())
                .set("umbrella", builderUmbrella().sample())
                .set("rentStoreMeta", builderStoreMeta().sample())
                .set("rentedAt", LocalDateTime.of(2021, 1, 1, 0, 0, 0))
                .set("returnedAt", LocalDateTime.of(2021, 1, 14, 0, 0, 0))
                .set("returnStoreMeta", builderStoreMeta().sample())
                .set("paidAt", LocalDateTime.of(2021, 1, 2, 0, 0, 0))
                .set("refundedAt", LocalDateTime.of(2021, 1, 15, 0, 0, 0))
                .set("refundedBy", builderUser().sample())
                .set("paidBy", builderUser().sample())
                .set("bank", pickRandomString(bankList))
                .set("accountNumber", pickAccountNumberString())
                .set("etc", pickRandomString(etcList));
    }
}
