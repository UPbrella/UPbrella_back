package upbrella.be.config;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.entity.User;

import java.util.List;

public class FixtureFactory {

    private static final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    public static int buildInteger() {

        return fixtureMonkey.giveMeOne(Integer.class);
    }

    public static long buildLong() {

        return fixtureMonkey.giveMeOne(Long.class);
    }

    public static StoreMeta buildStoreMeta() {

        return fixtureMonkey.giveMeBuilder(StoreMeta.class)
                .set("deleted", false)
                .sample();
    }

    public static StoreMeta buildStoreMetaWithId(long id) {

        return fixtureMonkey.giveMeBuilder(StoreMeta.class)
                .set("deleted", false)
                .set("id", id)
                .sample();
    }

    public static Umbrella buildUmbrella() {

        return fixtureMonkey.giveMeBuilder(Umbrella.class)
                .set("deleted", false)
                .sample();
    }

    public static Umbrella buildUmbrellaWithUmbrellaRequestAndStoreMeta(UmbrellaRequest umbrellaRequest, StoreMeta storeMeta) {

        return fixtureMonkey.giveMeBuilder(Umbrella.class)
                .set("deleted", false)
                .set("storeMeta", storeMeta)
                .set("uuid", umbrellaRequest.getUuid())
                .set("rentable", umbrellaRequest.isRentable())
                .sample();
    }

    public static Umbrella buildUmbrellaWithIdAndUmbrellaRequestAndStoreMeta(long id, UmbrellaRequest umbrellaRequest, StoreMeta storeMeta) {

        return fixtureMonkey.giveMeBuilder(Umbrella.class)
                .set("id", id)
                .set("deleted", false)
                .set("storeMeta", storeMeta)
                .set("uuid", umbrellaRequest.getUuid())
                .set("rentable", umbrellaRequest.isRentable())
                .sample();
    }

    public static List<Umbrella> buildUmbrellasWithStoreMeta(StoreMeta storeMeta, int size) {

        return fixtureMonkey.giveMeBuilder(Umbrella.class)
                .set("storeMeta", storeMeta)
                .sampleList(size);
    }

    public static UmbrellaResponse buildUmbrellaResponseWithUmbrellaAndStoreMeta(Umbrella umbrella, StoreMeta storeMeta) {

        return fixtureMonkey.giveMeBuilder(UmbrellaResponse.class)
                .set("id", umbrella.getId())
                .set("storeMetaId", storeMeta.getId())
                .set("uuid", umbrella.getUuid())
                .set("rentable", umbrella.isRentable())
                .sample();
    }

    public static List<UmbrellaResponse> buildUmbrellaResponses(int size) {

        return fixtureMonkey.giveMe(UmbrellaResponse.class, size);
    }


    public static UmbrellaRequest buildUmbrellaRequest() {

        return fixtureMonkey.giveMeOne(UmbrellaRequest.class);
    }

    public static User buildUser() {

        return fixtureMonkey.giveMeOne(User.class);
    }

    public static List<User> buildUsers(int size) {

        return fixtureMonkey.giveMe(User.class, size);
    }

    public static FixtureMonkey getInstance() {
        return fixtureMonkey;
    }

    public static JoinRequest buildJoinRequestWithUser(User user) {

        return fixtureMonkey.giveMeBuilder(JoinRequest.class)
                .set("name", user.getName())
                .set("phoneNumber", user.getPhoneNumber())
                .sample();
    }
}
