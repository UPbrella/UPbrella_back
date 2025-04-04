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
import upbrella.be.user.repository.BlackListRepository
import upbrella.be.user.repository.UserRepository
import upbrella.be.util.AesEncryptor
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val blackListRepository: BlackListRepository,
    @Lazy private val rentService: RentService,
    private val aesEncryptor: AesEncryptor
) {

    fun login(socialId: Long): SessionUser {
        val foundUser = userRepository.findBySocialId(socialId.hashCode().toLong())
            .orElseThrow { NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다. 회원 가입을 해주세요.") }

        return SessionUser.fromUser(foundUser)
    }

    fun join(kakaoUser: KakaoLoginResponse, joinRequest: JoinRequest): SessionUser {
        val socialIdHash = kakaoUser.id.hashCode().toLong()

        if (userRepository.existsBySocialId(socialIdHash)) {
            throw ExistingMemberException("[ERROR] 이미 가입된 회원입니다. 로그인 폼으로 이동합니다.")
        }

        if (blackListRepository.existsBySocialId(socialIdHash)) {
            throw BlackListUserException("[ERROR] 정지된 회원입니다. 정지된 회원은 재가입이 불가능합니다.")
        }

        val joinedUser = userRepository.save(
            User.createNewUser(kakaoUser, joinRequest, aesEncryptor)
        )

        return SessionUser.fromUser(joinedUser)
    }

    fun findUsers(): AllUsersInfoResponse {
        val users = userRepository.findAll()
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
        val foundUser = findUserById(id)
        foundUser.updateBankAccount(request.bank, request.accountNumber, aesEncryptor)
    }

    @Transactional
    fun deleteUser(id: Long) {
        val foundUser = findUserById(id)
        foundUser.deleteUser()
    }

    @Transactional
    fun withdrawUser(id: Long) {
        val foundUser = findUserById(id)
        val socialId = foundUser.socialId

        if (socialId == 0L) {
            throw NonExistingMemberException("[ERROR] 탈퇴하였거나 이미 블랙리스트 처리된 회원입니다.")
        }

        val newBlackList = BlackList.createNewBlackList(socialId, LocalDateTime.now())
        blackListRepository.save(newBlackList)

        foundUser.withdrawUser()
    }

    fun findUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다.") }
    }

    fun findDecryptedUserById(sessionUser: SessionUser): User {
        val id = sessionUser.id ?: throw NotLoginException("[ERROR] 로그인이 필요합니다.")

        return userRepository.findById(id)
            .orElseThrow { NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다.") }
            .decryptData(aesEncryptor)
    }

    @Transactional
    fun deleteUserBankAccount(id: Long) {
        val foundUser = findUserById(id)
        foundUser.deleteBankAccount()
    }

    @Transactional
    fun findBlackList(): AllBlackListResponse {
        val blackLists = blackListRepository.findAll()
        return AllBlackListResponse.of(blackLists)
    }

    @Transactional
    fun deleteBlackList(blackListId: Long) {
        blackListRepository.deleteById(blackListId)
    }

    @Transactional
    fun updateAdminStatus(id: Long) {
        val foundUser = findUserById(id)
        foundUser.updateAdminStatus()
    }

    @Transactional(readOnly = true)
    fun checkBlackList(userId: Long) {
        blackListRepository.findById(userId).ifPresent {
            throw BlackListUserException("[ERROR] 정지된 회원입니다. 정지된 회원은 이용이 불가능합니다.")
        }
    }
}
