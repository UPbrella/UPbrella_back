package upbrella.be.config;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import upbrella.be.rent.dto.response.RentalHistoryResponse;
import upbrella.be.rent.entity.History;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest;
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.response.KakaoLoginResponse;
import upbrella.be.user.dto.token.OauthToken;
import upbrella.be.user.entity.User;

import java.time.LocalDateTime;

import static upbrella.be.config.FixtureBuilderFactory.builderStoreMeta;
import static upbrella.be.config.FixtureBuilderFactory.builderUmbrella;

public class FixtureFactory {

    private static final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .defaultNotNull(true)
            .build();


    public static StoreMeta buildStoreMetaWithId(long id) {

        return builderStoreMeta()
                .set("id", id)
                .sample();
    }

    public static Umbrella buildUmbrellaWithUmbrellaRequestAndStoreMeta(UmbrellaCreateRequest umbrellaCreateRequest, StoreMeta storeMeta) {

        return builderUmbrella()
                .set("storeMeta", storeMeta)
                .set("uuid", umbrellaCreateRequest.getUuid())
                .set("rentable", umbrellaCreateRequest.isRentable())
                .sample();
    }

    public static Umbrella buildUmbrellaWithIdAndUmbrellaRequestAndStoreMeta(long id, UmbrellaModifyRequest umbrellaModifyRequest, StoreMeta storeMeta) {

        return builderUmbrella()
                .set("id", id)
                .set("storeMeta", storeMeta)
                .set("uuid", umbrellaModifyRequest.getUuid())
                .set("rentable", umbrellaModifyRequest.isRentable())
                .sample();
    }

    public static UmbrellaResponse buildUmbrellaResponseWithUmbrellaAndStoreMeta(Umbrella umbrella, StoreMeta storeMeta) {

        return fixtureMonkey.giveMeBuilder(UmbrellaResponse.class)
                .set("id", umbrella.getId())
                .set("storeMetaId", storeMeta.getId())
                .set("uuid", umbrella.getUuid())
                .set("rentable", umbrella.isRentable())
                .sample();
    }

    public static JoinRequest buildJoinRequestWithUser(User user) {

        return fixtureMonkey.giveMeBuilder(JoinRequest.class)
                .set("name", user.getName())
                .set("phoneNumber", user.getPhoneNumber())
                .sample();
    }

    public static History buildHistoryWithUmbrella(Umbrella umbrella) {

        return fixtureMonkey.giveMeBuilder(History.class)
                .set("umbrella", umbrella)
                .sample();
    }

    public static OauthToken buildOauthToken() {

        return fixtureMonkey.giveMeBuilder(OauthToken.class)
                .set("accessToken", "accessToken")
                .set("refreshToken", "refreshToken")
                .set("tokenType", "tokenType")
                .set("expiresIn", 1000L)
                .sample();
    }

    public static KakaoLoginResponse buildKakaoLoginResponse() {

        return fixtureMonkey.giveMeOne(KakaoLoginResponse.class);
    }

    public static RentalHistoryResponse buildRentalHistoryResponseWithHistory(History history) {

        return fixtureMonkey.giveMeBuilder(RentalHistoryResponse.class)
                .set("id", history.getId())
                .set("name", history.getUser().getName())
                .set("phoneNumber", history.getUser().getPhoneNumber())
                .set("rentStoreName", history.getRentStoreMeta().getName())
                .set("rentAt", history.getRentedAt())
                .set("elapsedDay", calElapsedDay(history))
                .set("umbrellaUuid", history.getUmbrella().getUuid())
                .set("returnStoreName", history.getReturnStoreMeta().getName())
                .set("returnAt", history.getReturnedAt())
                .set("totalRentalDay", history.getReturnedAt().getDayOfYear() - history.getRentedAt().getDayOfYear())
                .set("refundCompleted", true)
                .set("etc", history.getEtc())
                .sample();
    }

    private static int calElapsedDay(History history) {

        int elapsedDay = LocalDateTime.now().getDayOfYear() - history.getRentedAt().getDayOfYear();

        if (history.getReturnedAt() != null) {
            elapsedDay = history.getReturnedAt().getDayOfYear() - history.getRentedAt().getDayOfYear();
        }

        return elapsedDay;
    }

}
