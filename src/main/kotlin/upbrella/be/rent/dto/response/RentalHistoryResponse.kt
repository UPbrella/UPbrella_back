package upbrella.be.rent.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class RentalHistoryResponse(
    val id: Long,
    val name: String,
    val phoneNumber: String?,
    val rentStoreName: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    val rentAt: LocalDateTime,
    val elapsedDay: Int, // 경과 시간
    val umbrellaUuid: Long,
    val returnStoreName: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    val returnAt: LocalDateTime? = null,
    val totalRentalDay: Int? = null,
    val refundCompleted: Boolean,
    val paid: Boolean,
    val bank: String? = null,
    val accountNumber: String? = null,
    val etc: String? = null
) {
    companion object {

        fun createReturnedHistory(history: HistoryInfoDto, elapsedDay: Int, totalRentalDay: Int): RentalHistoryResponse {
            return RentalHistoryResponse(
                id = history.id,
                name = history.name,
                phoneNumber = history.phoneNumber,
                rentStoreName = history.rentStoreName,
                rentAt = history.rentAt,
                elapsedDay = elapsedDay,
                paid = history.paidAt != null,
                umbrellaUuid = history.umbrellaUuid,
                returnStoreName = history.returnStoreName,
                returnAt = history.returnAt,
                totalRentalDay = totalRentalDay,
                refundCompleted = history.refundedAt != null,
                bank = history.bank,
                accountNumber = history.accountNumber,
                etc = history.etc
            )
        }

        fun createNonReturnedHistory(history: HistoryInfoDto, elapsedDay: Int): RentalHistoryResponse {
            return RentalHistoryResponse(
                id = history.id,
                name = history.name,
                phoneNumber = history.phoneNumber,
                rentStoreName = history.rentStoreName,
                rentAt = history.rentAt,
                elapsedDay = elapsedDay,
                paid = history.paidAt != null,
                umbrellaUuid = history.umbrellaUuid,
                refundCompleted = history.refundedAt != null,
                bank = history.bank,
                accountNumber = history.accountNumber,
                etc = history.etc
            )
        }
    }
}
