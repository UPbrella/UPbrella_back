package upbrella.be.config;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.random.Randoms;
import net.jqwik.api.Arbitraries;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.response.SingleHistoryResponse;
import upbrella.be.user.entity.User;

public class FixtureBuilderFactory {

    private static final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .defaultNotNull(true)
            .build();

    private static String[] nameList = {"사과", "바나나", "딸기", "수박", "복숭아", "감자", "토마토", "오렌지", "키위", "자두", "귤도라지", "고구마", "파인애플", "레몬", "라임", "무화과", "치즈", "감자", "배추", "미역"};

    private static String pickRandomString() {

        return nameList[buildInteger() % nameList.length];
    }

    private static String pickNumberString() {

        return String.valueOf(Randoms.nextInt(1000000));
    }

    public static int buildInteger() {

        return Randoms.nextInt(1000000);
    }

    public static long buildLong() {

        return Randoms.nextInt(1000000);
    }

    public static ArbitraryBuilder<StoreMeta> builderStoreMeta() {

        return fixtureMonkey.giveMeBuilder(StoreMeta.class)
                .set("deleted", false)
                .set("id", buildLong())
                .set("name", pickRandomString())
                .set("address", pickRandomString())
                .set("category", pickRandomString())
                .set("password", pickRandomString())
                .set("latitude", Arbitraries.doubles().between(1, 100))
                .set("longitude", Arbitraries.doubles().between(1, 100));
    }

    public static ArbitraryBuilder<Umbrella> builderUmbrella() {

        return fixtureMonkey.giveMeBuilder(Umbrella.class)
                .set("id", buildLong())
                .set("uuid", buildLong())
                .set("storeMeta", builderStoreMeta().sample())
                .set("deleted", false);
    }
    public static ArbitraryBuilder<UmbrellaResponse> builderUmbrellaResponses() {

        return fixtureMonkey.giveMeBuilder(UmbrellaResponse.class)
                .set("id", buildLong())
                .set("storeMetaId", buildLong())
                .set("uuid", buildLong());
    }


    public static ArbitraryBuilder<UmbrellaRequest> builderUmbrellaRequest() {

        return fixtureMonkey.giveMeBuilder(UmbrellaRequest.class)
                .set("storeMetaId", buildLong())
                .set("uuid", buildLong());
    }

    public static ArbitraryBuilder<User> builderUser() {

        return fixtureMonkey.giveMeBuilder(User.class)
                .set("id", buildLong())
                .set("socialId", buildLong())
                .set("name", pickRandomString())
                .set("phoneNumber", pickNumberString())
                .set("bank", pickRandomString())
                .set("accountNumber", pickNumberString());
    }


    public static ArbitraryBuilder<SingleHistoryResponse> builderSingleHistoryResponse() {

        return fixtureMonkey.giveMeBuilder(SingleHistoryResponse.class)
                .set("umbrellaUuid", buildLong())
                .set("rentedStore", pickRandomString());
    }

    public static ArbitraryBuilder<JoinRequest> builderJoinRequest() {

        return fixtureMonkey.giveMeBuilder(JoinRequest.class)
                .set("name", pickRandomString())
                .set("phoneNumber", pickNumberString())
                .set("bank", pickRandomString())
                .set("accountNumber", pickNumberString());
    }
}
