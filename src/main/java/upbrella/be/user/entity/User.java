package upbrella.be.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
