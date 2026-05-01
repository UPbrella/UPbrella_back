package upbrella.be.rent.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.rent.dto.request.HistoryFilterRequest
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest
import upbrella.be.rent.dto.response.*
import upbrella.be.rent.entity.History
import upbrella.be.rent.event.UmbrellaRentedEvent
import upbrella.be.rent.exception.CannotBeRentedException
import upbrella.be.rent.exception.ExistingUmbrellaForRentException
import upbrella.be.rent.exception.NonExistingHistoryException
import upbrella.be.rent.exception.NonExistingUmbrellaForRentException
import upbrella.be.rent.repository.RentRepository
import upbrella.be.slack.SlackAlarmService
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.repository.StoreMetaReader
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.exception.NonExistingBorrowedHistoryException
import upbrella.be.umbrella.service.UmbrellaService
import upbrella.be.user.dto.response.AllHistoryResponse
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.user.dto.response.SingleHistoryResponse
import upbrella.be.user.entity.User
import upbrella.be.user.repository.UserReader
import upbrella.be.user.service.BlackListService
import upbrella.be.util.event.Events
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class RentService(
    private val umbrellaService: UmbrellaService,
    private val storeMetaReader: StoreMetaReader,
    private val slackAlarmService: SlackAlarmService,
    private val improvementReportService: ImprovementReportService,
    private val rentRepository: RentRepository,
    private val blackListService: BlackListService,
    private val userReader: UserReader,
) {

    fun findRentForm(umbrellaId: Long, user: User): RentFormResponse {
        val umbrella = umbrellaService.findUmbrellaById(umbrellaId)
        if (umbrella.cannotBeRented()) {
            throw CannotBeRentedException("[ERROR] 해당 우산은 대여 불가능한 우산입니다.")
        }
        return RentFormResponse.of(umbrella, user.hasPhoneNumber())
    }

    fun findReturnForm(
        storeId: Long,
        userToReturn: User
    ): ReturnFormResponse {
        val storeMeta: StoreMeta = storeMetaReader.findById(storeId)
        val history = rentRepository.findByUserIdAndReturnedAtIsNull(userToReturn.id!!)
            .orElseThrow { NonExistingUmbrellaForRentException("[ERROR] 해당 유저가 대여 중인 우산이 없습니다.") }
        return ReturnFormResponse.of(storeMeta, history)
    }

    @Transactional
    fun addRental(rentUmbrellaByUserRequest: RentUmbrellaByUserRequest, userToRent: User) {
        blackListService.checkBlackList(userToRent.id!!)
        rentRepository.findByUserIdAndReturnedAtIsNull(userToRent.id).ifPresent {
            throw ExistingUmbrellaForRentException("[ERROR] 해당 유저가 대여 중인 우산이 있습니다.")
        }

        if (!userToRent.hasPhoneNumber()) {
            userToRent.updatePhoneNumber(rentUmbrellaByUserRequest.phoneNumber)
        }

        val umbrella = umbrellaService.findUmbrellaById(rentUmbrellaByUserRequest.umbrellaId)

        umbrella.rentUmbrella(rentUmbrellaByUserRequest.storeId)

        val rentalStore = storeMetaReader.findById(rentUmbrellaByUserRequest.storeId)
        val history = rentRepository.save(
            History.ofCreatedByNewRent(umbrella, userToRent, rentalStore)
        )

        // umbrellaRentedEvent
        Events.raise(
            UmbrellaRentedEvent(
                userId = userToRent.id,
                userName = userToRent.name,
                rentStoreName = rentalStore.name,
                conditionReportContent = rentUmbrellaByUserRequest.conditionReport,
                umbrellaId = umbrella.id!!,
                historyId = history.id!!,
            )
        )
    }

    @Transactional
    fun returnUmbrellaByUser(userToReturn: User, request: ReturnUmbrellaByUserRequest) {
        blackListService.checkBlackList(userToReturn.id!!)
        val history = rentRepository.findByUserIdAndReturnedAtIsNull(userToReturn.id)
            .orElseThrow { NonExistingUmbrellaForRentException("[ERROR] 해당 유저가 대여 중인 우산이 없습니다.") }
        val returnStore = storeMetaReader.findById(request.returnStoreId)

        history.bank = request.bank
        history.accountNumber = request.accountNumber
        history.returnStoreMeta = returnStore
        history.returnedAt = LocalDateTime.now()

        val returnedUmbrella: Umbrella = history.umbrella
        returnedUmbrella.returnUmbrella(returnStore)

        rentRepository.save(history)
    }

    @Transactional
    fun findAllHistories(
        filter: HistoryFilterRequest,
        pageable: Pageable
    ): RentalHistoriesPageResponse {
        val countOfAllHistories = rentRepository.countAll(filter, pageable)
        val countOfAllPages = countOfAllHistories / pageable.pageSize
        val rentalHistories = findAllRentalHistory(filter, pageable)
        return RentalHistoriesPageResponse.of(rentalHistories, countOfAllHistories, countOfAllPages)
    }

    fun findAllHistoriesByUser(userId: Long): AllHistoryResponse =
        AllHistoryResponse.of(findAllByUserId(userId))

    private fun findAllByUserId(userId: Long): List<SingleHistoryResponse> =
        findAllByUser(userId).map { toSingleHistoryResponse(it) }

    private fun findAllByUser(userId: Long): List<History> =
        rentRepository.findAllByUserId(userId)

    private fun findAllRentalHistory(
        filter: HistoryFilterRequest,
        pageable: Pageable
    ): List<RentalHistoryResponse> =
        findHistoryInfos(filter, pageable).map { toRentalHistoryResponse(it) }

    private fun toSingleHistoryResponse(history: History): SingleHistoryResponse {
        var isReturned = true
        var isRefunded = false
        var returnAt: LocalDateTime? = history.returnedAt
        if (returnAt == null) {
            isReturned = false
            returnAt = history.rentedAt.plusDays(14)
        }
        if (history.refundedAt != null) {
            isRefunded = true
        }
        return SingleHistoryResponse.ofUserHistory(history, returnAt!!, isReturned, isRefunded)
    }

    private fun toRentalHistoryResponse(history: HistoryInfoDto): RentalHistoryResponse {
        var elapsedDay = ChronoUnit.DAYS.between(history.rentAt, LocalDateTime.now()).toInt()
        return if (history.returnAt != null) {
            elapsedDay = ChronoUnit.DAYS.between(
                history.rentAt.toLocalDate(),
                history.returnAt.toLocalDate()
            ).toInt()
            val totalRentalDay = ChronoUnit.DAYS.between(
                history.rentAt.toLocalDate(),
                history.returnAt.toLocalDate()
            ).toInt()
            RentalHistoryResponse.createReturnedHistory(history, elapsedDay, totalRentalDay)
        } else {
            RentalHistoryResponse.createNonReturnedHistory(history, elapsedDay)
        }
    }

    fun countTotalRent(): Long = rentRepository.count()

    fun countTotalRentByStoreId(storeId: Long): Long =
        rentRepository.countByRentStoreMetaId(storeId)

    fun countUnrefundedRent(): Long =
        rentRepository.countAllByReturnedAtIsNotNullAndPaidAtIsNotNullAndRefundedAtIsNull()

    @Transactional
    fun checkRefund(historyId: Long, userId: Long) {
        val loginedUser = userReader.findUserById(userId)
        val history = findHistoryById(historyId)
        history.refund(loginedUser, LocalDateTime.now())
        rentRepository.save(history)
    }

    @Transactional
    fun checkPayment(historyId: Long, userId: Long) {
        val loginedUser = userReader.findUserById(userId)
        val history = findHistoryById(historyId)
        history.paid(loginedUser, LocalDateTime.now())
        rentRepository.save(history)
    }

    private fun findHistoryById(historyId: Long): History =
        rentRepository.findById(historyId)
            .orElseThrow { NonExistingHistoryException("[ERROR] 해당 대여 기록이 없습니다.") }

    fun findRentalHistoryByUser(sessionUser: SessionUser): History =
        rentRepository.findByUserIdAndReturnedAtIsNull(sessionUser.id)
            .orElseThrow { NonExistingBorrowedHistoryException("[ERROR] 사용자가 빌린 우산이 없습니다.") }

    @Transactional
    fun deleteBankAccount(historyId: Long) {
        val history = findHistoryById(historyId)
        history.deleteBankAccount()
    }

    private fun findHistoryInfos(
        filter: HistoryFilterRequest,
        pageable: Pageable
    ): List<HistoryInfoDto> =
        rentRepository.findHistoryInfos(filter, pageable)
}
