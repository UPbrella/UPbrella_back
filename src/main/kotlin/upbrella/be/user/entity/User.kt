package upbrella.be.user.entity

import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.util.AesEncryptor
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
    var adminStatus: Boolean = false,
    var bank: String? = null,
    var accountNumber: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {

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
        this.socialId = 0L
        this.name = "탈퇴한 회원"
        this.phoneNumber = "deleted"
        this.adminStatus = false
        this.bank = null
        this.accountNumber = null
    }

    fun withdrawUser() {
        this.socialId = 0L
        this.name = "정지된 회원"
        this.phoneNumber = "deleted"
        this.email = "deleted"
        this.adminStatus = false
        this.bank = null
        this.accountNumber = null
    }

    fun decryptData(aesEncryptor: AesEncryptor): User {
        return User(
            socialId = socialId,
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            adminStatus = adminStatus,
            bank = aesEncryptor.decrypt(bank),
            accountNumber = aesEncryptor.decrypt(accountNumber),
            id = id
        )
    }

    fun deleteBankAccount() {
        this.bank = null
        this.accountNumber = null
    }

    fun updateAdminStatus() {
        this.adminStatus = !this.adminStatus
    }
}