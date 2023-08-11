package upbrella.be.user.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long socialId;

    public static BlackList createNewBlackList(long socialId) {

        return BlackList.builder()
                .socialId(socialId)
                .build();
    }
}
