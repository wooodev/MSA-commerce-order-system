package com.commerce.payment.domain.payment.controller

import com.commerce.payment.common.response.ApiResponse
import com.commerce.payment.domain.payment.dto.PaymentResponse
import com.commerce.payment.domain.payment.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @GetMapping("/orders/{orderId}")
    fun getPayment(
        @PathVariable orderId: Long,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<PaymentResponse>> {
        val response = paymentService.getPayment(orderId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
