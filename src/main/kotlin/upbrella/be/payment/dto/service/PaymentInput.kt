package upbrella.be.payment.dto.service

data class RequestPaymentInput(
    val amount: Long,
    val paymentKey: String,
    val orderId: String
)

data class CancelPaymentInput(
    val cancelReason: String,
    val cancelAmount: Long? = null,
    val cancelRequestId: String? = null,
    val currency: String? = null,
    val dividedPayment: Boolean? = null,
    val refundReceiveAccount: RefundReceiveAccount? = null,
    val taxAmount: Long? = null,
    val taxExemptionAmount: Long? = null,
    val taxFreeAmount: Long? = null,
)

data class RefundReceiveAccount(
    val accountNumber: String? = null,
    val bank: String? = null,
    val holderName: String? = null
)
