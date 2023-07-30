package upbrella.be.user.entity;

import lombok.*;
import upbrella.be.login.dto.request.JoinRequest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long socialId;
    private String name;
    private String phoneNumber;
    private boolean adminStatus;
    private String bank;
    private String accountNumber;
    private String authToken;

    public static User createNewUser(long userId, JoinRequest joinRequest) {
        return User.builder()
                .id(userId)
                .name(joinRequest.getName())
                .phoneNumber(joinRequest.getPhoneNumber())
                .adminStatus(false)
                .bank(joinRequest.getBank())
                .accountNumber(joinRequest.getAccountNumber())
                .socialId(joinRequest.getSocialId())
                .build();
    }

    public static User createNewUser(long socialId) {
        return User.builder()
                .socialId(socialId)
                .build();
    }

    @Builder
    protected User(String name, String phoneNumber, boolean adminStatus) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.adminStatus = adminStatus;
    }
}
