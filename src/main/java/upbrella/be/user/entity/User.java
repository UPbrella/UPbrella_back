package upbrella.be.user.entity;

import lombok.*;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.response.KakaoLoginResponse;
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
    private String email;
    private boolean adminStatus;
    private String bank;
    private String accountNumber;

    public static User createNewUser(KakaoLoginResponse kakaoUser, JoinRequest joinRequest, AesEncryptor aesEncryptor) {

        return User.builder()
                .socialId((long) kakaoUser.getId().hashCode())
                .name(joinRequest.getName())
                .phoneNumber(joinRequest.getPhoneNumber())
                .email(kakaoUser.getKakaoAccount().getEmail())
                .adminStatus(false)
                .bank(aesEncryptor.encrypt(joinRequest.getBank()))
                .accountNumber(aesEncryptor.encrypt(joinRequest.getAccountNumber()))
                .build();
    }

    public void updateBankAccount(String bank, String accountNumber, AesEncryptor aesEncryptor) {

        this.bank = aesEncryptor.encrypt(bank);
        this.accountNumber = aesEncryptor.encrypt(accountNumber);
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
        this.email = "deleted";
        this.adminStatus = false;
        this.bank = null;
        this.accountNumber = null;
    }

    public User decryptData(AesEncryptor aesEncryptor) {

        return User.builder()
                .id(this.id)
                .socialId(this.socialId)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .adminStatus(this.adminStatus)
                .email(this.email)
                .bank(aesEncryptor.decrypt(this.bank))
                .accountNumber(aesEncryptor.decrypt(this.accountNumber))
                .build();
    }

    public void deleteBankAccount() {

        this.bank = null;
        this.accountNumber = null;
    }

    public void updateAdminStatus() {

        this.adminStatus = !this.adminStatus;
    }
}
