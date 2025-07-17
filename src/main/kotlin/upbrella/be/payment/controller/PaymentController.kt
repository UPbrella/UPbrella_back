package upbrella.be.payment.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import upbrella.be.payment.dto.controller.RequestPaymentRequest
import upbrella.be.payment.dto.service.RequestPaymentInput
import upbrella.be.payment.service.PaymentService

@RestController
class PaymentController(
    private val paymentService: PaymentService,
    private val objectMapper: ObjectMapper,
) {
    @PostMapping("/v1/payments")
    fun requestPayment(
        @RequestBody request: RequestPaymentRequest
    ) {
        val input = objectMapper.convertValue<RequestPaymentInput>(request)
        val output = paymentService.requestPayment(input)
    }
}