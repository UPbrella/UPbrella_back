package upbrella.be.user.entity;

import lombok.*;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.util.AesEncryptor;

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
    private Long socialId;
    private String name;
    private String phoneNumber;
    private boolean adminStatus;
    private String bank;
    private String accountNumber;

    public static User createNewUser(Long socialId, JoinRequest joinRequest) {

        return User.builder()
                .socialId((long) socialId.hashCode())
                .name(joinRequest.getName())
                .phoneNumber(joinRequest.getPhoneNumber())
                .adminStatus(false)
                .bank(AesEncryptor.encrypt(joinRequest.getBank()))
                .accountNumber(AesEncryptor.encrypt(joinRequest.getAccountNumber()))
                .build();
    }

    public void updateBankAccount(String bank, String accountNumber) {

        this.bank = AesEncryptor.encrypt(bank);
        this.accountNumber = AesEncryptor.encrypt(accountNumber);
    }

    public void deleteUser() {

        this.socialId = 0L;
        this.name = "탈퇴한 회원";
        this.phoneNumber = "deleted";
        this.adminStatus = false;
        this.bank = null;
        this.accountNumber = null;
    }

    public void withdrawUser() {

        this.socialId = 0L;
        this.name = "정지된 회원";
        this.phoneNumber = "deleted";
        this.adminStatus = false;
        this.bank = null;
        this.accountNumber = null;
    }

    public User decryptData() {

        return User.builder()
                .id(this.id)
                .socialId(this.socialId)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .adminStatus(this.adminStatus)
                .bank(AesEncryptor.decrypt(this.bank))
                .accountNumber(AesEncryptor.decrypt(this.accountNumber))
                .build();
    }
}
