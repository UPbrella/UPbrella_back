package upbrella.be.config;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import upbrella.be.rent.dto.response.HistoryInfoDto;
import upbrella.be.rent.dto.response.RentalHistoryResponse;
import upbrella.be.rent.entity.History;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest;
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory;
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

    public static UmbrellaResponse buildUmbrellaResponseWithUmbrellaAndStoreMeta(UmbrellaWithHistory umbrella, StoreMeta storeMeta) {

        return fixtureMonkey.giveMeBuilder(UmbrellaResponse.class)
                .set("id", umbrella.getId())
                .set("storeMetaId", storeMeta.getId())
                .set("uuid", umbrella.getUuid())
                .set("rentable", umbrella.isRentable())
                .set("storeName", storeMeta.getName())
                .set("historyId", umbrella.getHistoryId())
                .set("etc", umbrella.getEtc())
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

    public static RentalHistoryResponse buildRentalHistoryResponseWithHistory(HistoryInfoDto history) {

        return fixtureMonkey.giveMeBuilder(RentalHistoryResponse.class)
                .set("id", history.getId())
                .set("name", history.getName())
                .set("phoneNumber", history.getPhoneNumber())
                .set("rentStoreName", history.getRentStoreName())
                .set("rentAt", history.getRentAt())
                .set("elapsedDay", calElapsedDay(history))
                .set("paid", history.getPaidAt() != null)
                .set("umbrellaUuid", history.getUmbrellaUuid())
                .set("returnStoreName", history.getReturnStoreName())
                .set("returnAt", history.getReturnAt())
                .set("totalRentalDay", history.getReturnAt().getDayOfYear() - history.getRentAt().getDayOfYear())
                .set("refundCompleted", true)
                .set("bank", history.getBank())
                .set("accountNumber", history.getAccountNumber())
                .set("etc", history.getEtc())
                .sample();
    }

    private static int calElapsedDay(HistoryInfoDto history) {

        int elapsedDay = LocalDateTime.now().getDayOfYear() - history.getRentAt().getDayOfYear();

        if (history.getReturnAt() != null) {
            elapsedDay = history.getReturnAt().getDayOfYear() - history.getRentAt().getDayOfYear();
        }

        return elapsedDay;
    }

}
