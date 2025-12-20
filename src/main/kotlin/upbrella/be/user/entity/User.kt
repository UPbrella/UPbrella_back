package upbrella.be.user.entity

import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.response.AppleLoginResponse
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.util.AesEncryptor
import upbrella.be.util.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User(
    var socialId: Long,
    var name: String,
    var phoneNumber: String,
    var email: String,
    var provider: String = "KAKAO", // KAKAO or APPLE
    var adminStatus: Boolean = false,
    var bank: String? = null,
    var accountNumber: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : BaseTimeEntity() {

    companion object {
        fun createNewUser(
            kakaoUser: KakaoLoginResponse,
            joinRequest: JoinRequest,
            aesEncryptor: AesEncryptor
        ): User {
            return User(
                socialId = kakaoUser.id.hashCode().toLong(),
                name = joinRequest.name,
                phoneNumber = joinRequest.phoneNumber,
                email = kakaoUser.kakaoAccount?.email ?: "",
                provider = "KAKAO",
                bank = aesEncryptor.encrypt(joinRequest.bank),
                accountNumber = aesEncryptor.encrypt(joinRequest.accountNumber)
            )
        }

        fun createNewAppleUser(
            appleUser: AppleLoginResponse,
            joinRequest: JoinRequest,
            aesEncryptor: AesEncryptor
        ): User {
            return User(
                socialId = appleUser.sub.hashCode().toLong(),
                // Apple에서 이름을 제공한 경우 사용, 없으면 회원가입 폼의 이름 사용
                name = appleUser.name?.takeIf { it.isNotBlank() } ?: joinRequest.name,
                phoneNumber = joinRequest.phoneNumber,
                // Apple에서 이메일을 제공한 경우 사용, 없으면 회원가입 폼의 이메일 사용
                email = appleUser.email ?: joinRequest.email ?: "",
                provider = "APPLE",
                bank = aesEncryptor.encrypt(joinRequest.bank),
                accountNumber = aesEncryptor.encrypt(joinRequest.accountNumber)
            )
        }
    }


    fun updateBankAccount(bank: String, accountNumber: String, aesEncryptor: AesEncryptor) {
        this.bank = aesEncryptor.encrypt(bank)
        this.accountNumber = aesEncryptor.encrypt(accountNumber)
    }

    fun deleteUser() {
        // socialId unique constraint 위반 방지를 위해 음수 ID 사용 (DB ID를 음수로 변환)
        this.socialId = -(this.id ?: System.currentTimeMillis())
        this.name = "탈퇴한 회원"
        this.phoneNumber = "deleted"
        this.email = "deleted"
        this.adminStatus = false
        this.bank = null
        this.accountNumber = null
    }

    fun withdrawUser() {
        // socialId unique constraint 위반 방지를 위해 음수 ID 사용 (DB ID를 음수로 변환)
        this.socialId = -(this.id ?: System.currentTimeMillis())
        this.name = "정지된 회원"
        this.phoneNumber = "deleted"
        this.email = "deleted"
        this.adminStatus = false
        this.bank = null
        this.accountNumber = null
    }

    fun decryptData(aesEncryptor: AesEncryptor) {
        this.bank = aesEncryptor.decrypt(bank)
        this.accountNumber = aesEncryptor.decrypt(accountNumber)
    }

    fun deleteBankAccount() {
        this.bank = null
        this.accountNumber = null
    }

    fun updateAdminStatus() {
        this.adminStatus = !this.adminStatus
    }
}