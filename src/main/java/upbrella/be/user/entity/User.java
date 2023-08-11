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

    public void updateBankAccount(String bank, String accountNumber) {

        this.bank = bank;
        this.accountNumber = accountNumber;
    }

    public void deleteUser() {

        this.socialId = 0;
        this.name = "탈퇴한 회원";
        this.phoneNumber = "deleted";
        this.adminStatus = false;
        this.bank = null;
        this.accountNumber = null;
    }

    public void withdrawUser() {

        this.socialId = 0;
        this.name = "정지된 회원";
        this.phoneNumber = "deleted";
        this.adminStatus = false;
        this.bank = null;
        this.accountNumber = null;
    }
}
