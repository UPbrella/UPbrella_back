package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UmbrellaBorrowedByUserResponse {

    private long uuid;
    private int elapsedDay;

    public static UmbrellaBorrowedByUserResponse of(long uuid, int elapsedDay) {

        return UmbrellaBorrowedByUserResponse.builder()
                .uuid(uuid)
                .elapsedDay(elapsedDay)
                .build();
    }
}
