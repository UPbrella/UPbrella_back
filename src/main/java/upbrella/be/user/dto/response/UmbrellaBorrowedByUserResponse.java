package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UmbrellaBorrowedByUserResponse {

    private long uuid;

    public static UmbrellaBorrowedByUserResponse of(long uuid) {

        return UmbrellaBorrowedByUserResponse.builder()
                .uuid(uuid)
                .build();
    }
}
