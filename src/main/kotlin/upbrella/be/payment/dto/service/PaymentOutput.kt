package upbrella.be.payment.dto.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class CancelPaymentOutput(
    val paymentKey: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestPaymentOutput(
    @JsonProperty("mId")
    val mId: String,

    @JsonProperty("lastTransactionKey")
    val lastTransactionKey: String,

    @JsonProperty("paymentKey")
    val paymentKey: String,

    @JsonProperty("orderId")
    val orderId: String,

    @JsonProperty("orderName")
    val orderName: String,

    @JsonProperty("taxExemptionAmount")
    val taxExemptionAmount: Long,

    @JsonProperty("status")
    val status: String,

    @JsonProperty("requestedAt")
    val requestedAt: String,

    @JsonProperty("approvedAt")
    val approvedAt: String?,

    @JsonProperty("useEscrow")
    val useEscrow: Boolean,

    @JsonProperty("cultureExpense")
    val cultureExpense: Boolean,

    @JsonProperty("card")
    val card: TossCardInfo? = null,

    @JsonProperty("virtualAccount")
    val virtualAccount: TossVirtualAccountInfo? = null,

    @JsonProperty("transfer")
    val transfer: TossTransferInfo? = null,

    @JsonProperty("mobilePhone")
    val mobilePhone: TossMobilePhoneInfo? = null,

    @JsonProperty("giftCertificate")
    val giftCertificate: TossGiftCertificateInfo? = null,

    @JsonProperty("cashReceipt")
    val cashReceipt: TossCashReceiptInfo? = null,

    @JsonProperty("cashReceipts")
    val cashReceipts: List<TossCashReceiptInfo>? = null,

    @JsonProperty("discount")
    val discount: TossDiscountInfo? = null,

    @JsonProperty("cancels")
    val cancels: List<TossCancelInfo>? = null,

    @JsonProperty("secret")
    val secret: String?,

    @JsonProperty("type")
    val type: String,

    @JsonProperty("easyPay")
    val easyPay: TossEasyPayInfo? = null,

    @JsonProperty("country")
    val country: String,

    @JsonProperty("failure")
    val failure: TossFailureInfo? = null,

    @JsonProperty("isPartialCancelable")
    val isPartialCancelable: Boolean,

    @JsonProperty("receipt")
    val receipt: TossReceiptInfo? = null,

    @JsonProperty("checkout")
    val checkout: TossCheckoutInfo? = null,

    @JsonProperty("currency")
    val currency: String,

    @JsonProperty("totalAmount")
    val totalAmount: Long,

    @JsonProperty("balanceAmount")
    val balanceAmount: Long,

    @JsonProperty("suppliedAmount")
    val suppliedAmount: Long,

    @JsonProperty("vat")
    val vat: Long,

    @JsonProperty("taxFreeAmount")
    val taxFreeAmount: Long,

    @JsonProperty("metadata")
    val metadata: Map<String, Any>? = null,

    @JsonProperty("method")
    val method: String,

    @JsonProperty("version")
    val version: String
)

/**
 * 카드 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossCardInfo(
    @JsonProperty("issuerCode")
    val issuerCode: String,

    @JsonProperty("acquirerCode")
    val acquirerCode: String,

    @JsonProperty("number")
    val number: String,

    @JsonProperty("installmentPlanMonths")
    val installmentPlanMonths: Int,

    @JsonProperty("isInterestFree")
    val isInterestFree: Boolean,

    @JsonProperty("interestPayer")
    val interestPayer: String?,

    @JsonProperty("approveNo")
    val approveNo: String,

    @JsonProperty("useCardPoint")
    val useCardPoint: Boolean,

    @JsonProperty("cardType")
    val cardType: String,

    @JsonProperty("ownerType")
    val ownerType: String,

    @JsonProperty("acquireStatus")
    val acquireStatus: String,

    @JsonProperty("amount")
    val amount: Long
)

/**
 * 간편결제 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossEasyPayInfo(
    @JsonProperty("provider")
    val provider: String,

    @JsonProperty("amount")
    val amount: Long,

    @JsonProperty("discountAmount")
    val discountAmount: Long
)

/**
 * 가상계좌 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossVirtualAccountInfo(
    @JsonProperty("accountNumber")
    val accountNumber: String,

    @JsonProperty("accountType")
    val accountType: String,

    @JsonProperty("bankCode")
    val bankCode: String,

    @JsonProperty("customerName")
    val customerName: String,

    @JsonProperty("dueDate")
    val dueDate: String,

    @JsonProperty("expired")
    val expired: Boolean,

    @JsonProperty("settlementStatus")
    val settlementStatus: String,

    @JsonProperty("refundStatus")
    val refundStatus: String,

    @JsonProperty("refundReceiveAccount")
    val refundReceiveAccount: TossRefundReceiveAccountInfo?
)

/**
 * 계좌이체 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossTransferInfo(
    @JsonProperty("bankCode")
    val bankCode: String,

    @JsonProperty("settlementStatus")
    val settlementStatus: String
)

/**
 * 휴대폰 결제 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossMobilePhoneInfo(
    @JsonProperty("customerMobilePhone")
    val customerMobilePhone: String,

    @JsonProperty("settlementStatus")
    val settlementStatus: String,

    @JsonProperty("receiptUrl")
    val receiptUrl: String
)

/**
 * 상품권 결제 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossGiftCertificateInfo(
    @JsonProperty("approveNo")
    val approveNo: String,

    @JsonProperty("settlementStatus")
    val settlementStatus: String
)

/**
 * 현금영수증 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossCashReceiptInfo(
    @JsonProperty("type")
    val type: String,

    @JsonProperty("receiptKey")
    val receiptKey: String,

    @JsonProperty("issueNumber")
    val issueNumber: String,

    @JsonProperty("receiptUrl")
    val receiptUrl: String,

    @JsonProperty("amount")
    val amount: Long,

    @JsonProperty("taxFreeAmount")
    val taxFreeAmount: Long
)

/**
 * 할인 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossDiscountInfo(
    @JsonProperty("amount")
    val amount: Long
)


/**
 * 취소 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossCancelInfo(
    @JsonProperty("cancelAmount")
    val cancelAmount: Long,

    @JsonProperty("cancelReason")
    val cancelReason: String,

    @JsonProperty("taxFreeAmount")
    val taxFreeAmount: Long,

    @JsonProperty("taxExemptionAmount")
    val taxExemptionAmount: Long,

    @JsonProperty("refundableAmount")
    val refundableAmount: Long,

    @JsonProperty("easyPayDiscountAmount")
    val easyPayDiscountAmount: Long,

    @JsonProperty("canceledAt")
    val canceledAt: String,

    @JsonProperty("transactionKey")
    val transactionKey: String,

    @JsonProperty("receiptKey")
    val receiptKey: String?
)

/**
 * 실패 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossFailureInfo(
    @JsonProperty("code")
    val code: String,

    @JsonProperty("message")
    val message: String
)

/**
 * 영수증 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossReceiptInfo(
    @JsonProperty("url")
    val url: String
)

/**
 * 결제창 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossCheckoutInfo(
    @JsonProperty("url")
    val url: String
)

/**
 * 환불 받을 계좌 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossRefundReceiveAccountInfo(
    @JsonProperty("bankCode")
    val bankCode: String,

    @JsonProperty("accountNumber")
    val accountNumber: String,

    @JsonProperty("holderName")
    val holderName: String
)

/**
 * 결제 승인 요청 DTO
 */
data class TossPaymentConfirmRequest(
    @JsonProperty("paymentKey")
    val paymentKey: String,

    @JsonProperty("orderId")
    val orderId: String,

    @JsonProperty("amount")
    val amount: Long
)

/**
 * 결제 취소 요청 DTO
 */
data class TossPaymentCancelRequest(
    @JsonProperty("cancelReason")
    val cancelReason: String,

    @JsonProperty("cancelAmount")
    val cancelAmount: Long? = null, // null이면 전액 취소

    @JsonProperty("refundReceiveAccount")
    val refundReceiveAccount: TossRefundReceiveAccountInfo? = null
)
