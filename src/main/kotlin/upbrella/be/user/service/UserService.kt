package upbrella.be.user.service

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.rent.entity.History
import upbrella.be.rent.service.RentService
import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.request.UpdateBankAccountRequest
import upbrella.be.user.dto.response.*
import upbrella.be.user.entity.BlackList
import upbrella.be.user.entity.User
import upbrella.be.user.exception.*
import upbrella.be.user.repository.*
import upbrella.be.util.AesEncryptor
import java.time.LocalDateTime

@Service
class UserService(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    @Lazy private val rentService: RentService,
    private val aesEncryptor: AesEncryptor,
    private val userRepository: UserRepository,
    private val blackListReader: BlackListReader,
    private val blackListWriter: BlackListWriter,
) {

    fun login(socialId: Long): SessionUser {
        val foundUser = userReader.findBySocialId(socialId)

        return SessionUser.fromUser(foundUser)
    }

    fun join(kakaoUser: KakaoLoginResponse, joinRequest: JoinRequest): SessionUser {
        val socialIdHash = kakaoUser.id.hashCode().toLong()

        if (userReader.existsBySocialId(socialIdHash)) {
            throw ExistingMemberException("[ERROR] 이미 가입된 회원입니다. 로그인 폼으로 이동합니다.")
        }

        if (blackListReader.existsBySocialId(socialIdHash)) {
            throw BlackListUserException("[ERROR] 정지된 회원입니다. 정지된 회원은 재가입이 불가능합니다.")
        }

        val joinedUser = userWriter.save(
            User.createNewUser(kakaoUser, joinRequest, aesEncryptor)
        )

        return SessionUser.fromUser(joinedUser)
    }

    fun findUsers(): AllUsersInfoResponse {
        val users = userReader.findAll()
            .map { it.decryptData(aesEncryptor) }

        return AllUsersInfoResponse.fromUsers(users)
    }

    fun findUmbrellaBorrowedByUser(sessionUser: SessionUser): UmbrellaBorrowedByUserResponse {
        val rentalHistory: History = rentService.findRentalHistoryByUser(sessionUser)

        val borrowedUmbrellaUuid = rentalHistory.umbrella.uuid
        val elapsedDay = LocalDateTime.now().dayOfYear - rentalHistory.rentedAt.dayOfYear

        return UmbrellaBorrowedByUserResponse.of(borrowedUmbrellaUuid, elapsedDay)
    }

    @Transactional
    fun updateUserBankAccount(id: Long, request: UpdateBankAccountRequest) {
        val foundUser = userReader.findUserById(id)
        foundUser.updateBankAccount(request.bank, request.accountNumber, aesEncryptor)
    }

    @Transactional
    fun deleteUser(id: Long) {
        val foundUser = userReader.findUserById(id)
        foundUser.deleteUser()
    }

    @Transactional
    fun withdrawUser(id: Long) {
        val foundUser = userReader.findUserById(id)
        val socialId = foundUser.socialId

        if (socialId == 0L) {
            throw NonExistingMemberException("[ERROR] 탈퇴하였거나 이미 블랙리스트 처리된 회원입니다.")
        }

        val newBlackList = BlackList.createNewBlackList(socialId, LocalDateTime.now())
        blackListWriter.save(newBlackList)

        foundUser.withdrawUser()
    }

    fun findDecryptedUserById(sessionUser: SessionUser): User {
        val id = sessionUser.id

        return userRepository.findById(id)
            .orElseThrow { NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다.") }
            .decryptData(aesEncryptor)
    }

    @Transactional
    fun deleteUserBankAccount(id: Long) {
        val foundUser = userReader.findUserById(id)
        foundUser.deleteBankAccount()
    }
    @Transactional
    fun updateAdminStatus(id: Long) {
        val foundUser = userReader.findUserById(id)
        foundUser.updateAdminStatus()
    }
}
