package upbrella.be.user.entity;

import lombok.*;
import upbrella.be.user.dto.request.JoinRequest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private long socialId;
    private String name;
    private String phoneNumber;
    private boolean adminStatus;
    private String bank;
    private String accountNumber;

    public static User createNewUser(long socialId, JoinRequest joinRequest) {

        return User.builder()
                .socialId(socialId)
                .name(joinRequest.getName())
                .phoneNumber(joinRequest.getPhoneNumber())
                .adminStatus(false)
                .bank(joinRequest.getBank())
                .accountNumber(joinRequest.getAccountNumber())
                .build();
    }
}
