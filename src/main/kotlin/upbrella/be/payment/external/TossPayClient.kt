package upbrella.be.payment.external

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import upbrella.be.payment.dto.service.CancelPaymentInput
import upbrella.be.payment.dto.service.CancelPaymentOutput
import upbrella.be.payment.dto.service.RequestPaymentInput
import upbrella.be.payment.dto.service.RequestPaymentOutput


@FeignClient(
    name = "toss-payment",
    url = "https://api.tosspayments.com"
)
interface TossPayClient {
    
    @PostMapping("/v1/payments/confirm")
    fun requestPayment(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody paymentInput: RequestPaymentInput
    ): RequestPaymentOutput


    @PostMapping("/v1/payments/{paymentKey}/cancel")
    fun cancelPayment(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable paymentKey: String,
        @RequestBody cancelRequest: CancelPaymentInput
    ): CancelPaymentOutput
}
