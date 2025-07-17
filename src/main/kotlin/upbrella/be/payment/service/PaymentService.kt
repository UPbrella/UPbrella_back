package upbrella.be.payment.service

import upbrella.be.payment.dto.service.RequestPaymentInput
import upbrella.be.payment.dto.service.RequestPaymentOutput

interface PaymentService {
    // 결제 요청
    fun requestPayment(paymentInput: RequestPaymentInput): RequestPaymentOutput
    // 결제 취소
    fun cancelPayment()
}