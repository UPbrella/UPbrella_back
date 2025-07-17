package upbrella.be.payment.dto.controller

data class RequestPaymentRequest(
    val amount: Long,
    val paymentKey: String,
    val orderId: String
)