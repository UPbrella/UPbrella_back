package upbrella.be.payment.service

import org.springframework.stereotype.Service
import upbrella.be.payment.dto.service.RequestPaymentInput
import upbrella.be.payment.dto.service.RequestPaymentOutput
import upbrella.be.payment.external.TossPayClient

@Service
class TossPayService(
    private val tossPayClient: TossPayClient,
) : PaymentService {

    private val authorization = "dGVzdF9za19EbnlScFFXR3JONW1OWkphWmE5ZVZLd3YxTTlFOg=="

    // paymentKey에 해당하는 결제를 검증하고 승인합니다.
    // 결제 인증이 유효한 10분 안에 상점에서 결제 승인 API를 호출하지 않으면 해당 결제는 만료됩니다.
    // amount, orderId, paymentKey 필수 파라미터
    override fun requestPayment(paymentInput: RequestPaymentInput): RequestPaymentOutput {
        return tossPayClient.requestPayment(
            authorization = authorization,
            paymentInput
        )
    }

    override fun cancelPayment() {
    }
}