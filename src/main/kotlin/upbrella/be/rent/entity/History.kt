package upbrella.be.rent.entity

import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest
import upbrella.be.rent.exception.NotRefundedException
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.user.dto.response.SingleHistoryResponse
import upbrella.be.user.entity.User
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class History(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "umbrella_id")
    val umbrella: Umbrella,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
    var paidAt: LocalDateTime? = null,

    var bank: String? = null,
    var accountNumber: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rent_store_meta_id")
    val rentStoreMeta: StoreMeta,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_store_meta_id")
    val returnStoreMeta: StoreMeta? = null,

    val rentedAt: LocalDateTime = LocalDateTime.now(),
    var returnedAt: LocalDateTime? = null,
    var refundedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refunded_by")
    var refundedBy: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by")
    var paidBy: User? = null,

    var etc: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {

    companion object {
        @JvmStatic
        fun ofCreatedByNewRent(umbrella: Umbrella, user: User, rentStoreMeta: StoreMeta): History {
            return History(
                umbrella = umbrella,
                user = user,
                rentStoreMeta = rentStoreMeta,
            )
        }

        @JvmStatic
        fun updateHistoryForReturn(
            rentedHistory: History,
            returnStoreMeta: StoreMeta,
            request: ReturnUmbrellaByUserRequest
        ): History {
            return History(
                id = rentedHistory.id,
                umbrella =  rentedHistory.umbrella,
                user = rentedHistory.user,
                paidAt =  rentedHistory.paidAt,
                bank = request.bank,
                accountNumber = request.accountNumber,
                rentStoreMeta = rentedHistory.rentStoreMeta,
                returnStoreMeta = returnStoreMeta,
                rentedAt = rentedHistory.rentedAt,
                returnedAt = LocalDateTime.now(),
            )
        }

        fun ofUserHistory(history: History): SingleHistoryResponse {
            var isReturned = true
            var isRefunded = false

            var returnAt = history.returnedAt

            if (returnAt == null) {
                isReturned = false
                returnAt = history.rentedAt.plusDays(7)
            }

            if (history.refundedAt != null) {
                isRefunded = true
            }

            return SingleHistoryResponse.builder()
                .umbrellaUuid(history.umbrella.uuid)
                .rentedAt(history.rentedAt)
                .rentedStore(history.rentStoreMeta.name)
                .returnAt(returnAt)
                .isReturned(isReturned)
                .isRefunded(isRefunded)
                .build()
        }
    }

    fun refund(user: User, refundedAt: LocalDateTime) {
        if (this.refundedAt != null || this.refundedBy != null) {
            this.refundedAt = null
            this.refundedBy = null
            return
        }
        this.refundedAt = refundedAt
        this.refundedBy = user
    }

    fun paid(user: User, paidAt: LocalDateTime) {
        if (this.paidAt != null || this.paidBy != null) {
            this.paidAt = null
            this.paidBy = null
            return
        }
        this.paidAt = paidAt
        this.paidBy = user
    }

    fun deleteBankAccount() {
        if (this.refundedAt == null) {
            throw NotRefundedException("[ERROR] 보증금 환급이 완료되지 않았습니다.")
        }
        this.bank = null
        this.accountNumber = null
    }
}