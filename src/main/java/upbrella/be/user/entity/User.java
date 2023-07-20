package upbrella.be.user.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String phoneNumber;
    private boolean adminStatus;

    public static User createNewUser(String userName, String phoneNumber) {
        return User.builder()
                .name(userName)
                .phoneNumber(phoneNumber)
                .adminStatus(false)
                .build();
    }

    @Builder
    protected User(String name, String phoneNumber, boolean adminStatus) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.adminStatus = adminStatus;
    }
}
