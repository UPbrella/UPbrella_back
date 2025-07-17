package upbrella.be.payment.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(
    name = "payments",
)
class Payment(
    @Id
    @Column(name = "payment_key", length = 200)
    val paymentKey: String,

    // 기본 결제 정보
    @Column(name = "order_id", length = 200, unique = true, nullable = false)
    val orderId: String,

    @Column(name = "user_id", length = 100, nullable = false)
    val userId: String, // 내 서비스의 사용자 ID (별도 추가)

    @Column(name = "m_id", length = 50)
    val mId: String,

    @Column(name = "last_transaction_key", length = 100)
    val lastTransactionKey: String,

    @Column(name = "order_name", length = 500)
    val orderName: String,

    @Column(name = "status", length = 50)
    val status: String,

    @Column(name = "method", length = 50)
    val method: String,

    @Column(name = "type", length = 50)
    val type: String,

    @Column(name = "country", length = 10)
    val country: String,

    @Column(name = "currency", length = 10)
    val currency: String,

    @Column(name = "version", length = 20)
    val version: String,

    // 금액 정보
    @Column(name = "total_amount")
    val totalAmount: Long,

    @Column(name = "balance_amount")
    val balanceAmount: Long,

    @Column(name = "supplied_amount")
    val suppliedAmount: Long,

    @Column(name = "vat")
    val vat: Long,

    @Column(name = "tax_free_amount")
    val taxFreeAmount: Long,

    @Column(name = "tax_exemption_amount")
    val taxExemptionAmount: Long,

    // 시간 정보
    @Column(name = "requested_at")
    val requestedAt: LocalDateTime,

    @Column(name = "approved_at")
    val approvedAt: LocalDateTime?,

    // 플래그 정보
    @Column(name = "use_escrow")
    val useEscrow: Boolean,

    @Column(name = "culture_expense")
    val cultureExpense: Boolean,

    @Column(name = "is_partial_cancelable")
    val isPartialCancelable: Boolean,

    // 카드 정보 (card 객체가 있을 때만)
    @Column(name = "card_issuer_code", length = 10)
    val cardIssuerCode: String?,

    @Column(name = "card_acquirer_code", length = 10)
    val cardAcquirerCode: String?,

    @Column(name = "card_number", length = 20)
    val cardNumber: String?,

    @Column(name = "card_installment_plan_months")
    val cardInstallmentPlanMonths: Int?,

    @Column(name = "card_is_interest_free")
    val cardIsInterestFree: Boolean?,

    @Column(name = "card_interest_payer", length = 50)
    val cardInterestPayer: String?,

    @Column(name = "card_approve_no", length = 20)
    val cardApproveNo: String?,

    @Column(name = "card_use_card_point")
    val cardUseCardPoint: Boolean?,

    @Column(name = "card_type", length = 20)
    val cardType: String?,

    @Column(name = "card_owner_type", length = 20)
    val cardOwnerType: String?,

    @Column(name = "card_acquire_status", length = 20)
    val cardAcquireStatus: String?,

    @Column(name = "card_amount")
    val cardAmount: Long?,

    // 간편결제 정보 (easyPay 객체가 있을 때만)
    @Column(name = "easy_pay_provider", length = 50)
    val easyPayProvider: String?,

    @Column(name = "easy_pay_amount")
    val easyPayAmount: Long?,

    @Column(name = "easy_pay_discount_amount")
    val easyPayDiscountAmount: Long?,

    // URL 정보
    @Column(name = "receipt_url", length = 500)
    val receiptUrl: String?,

    @Column(name = "checkout_url", length = 500)
    val checkoutUrl: String?,

    // 메타데이터 (JSON 형태로 저장)
    @Column(name = "metadata", columnDefinition = "TEXT")
    val metadata: String?,

    // 원본 응답 백업 (JSON 형태로 저장)
    @Column(name = "raw_response", columnDefinition = "TEXT")
    val rawResponse: String?,

    // 시스템 정보
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {

    fun isCompleted(): Boolean = status == "DONE"
    fun isCanceled(): Boolean = status == "CANCELED"
    fun isCardPayment(): Boolean = method == "카드"
    fun isVirtualAccountPayment(): Boolean = method == "가상계좌"
}
