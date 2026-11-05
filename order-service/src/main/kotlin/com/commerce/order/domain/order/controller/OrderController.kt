package com.commerce.order.domain.order.controller

import com.commerce.order.common.response.ApiResponse
import com.commerce.order.domain.order.dto.CreateOrderRequest
import com.commerce.order.domain.order.dto.OrderResponse
import com.commerce.order.domain.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(
        @RequestBody @Valid request: CreateOrderRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val response = orderService.createOrder(request, user.username.toLong())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response))
    }

    @GetMapping("/{orderId}")
    fun getOrder(
        @PathVariable orderId: Long,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val response = orderService.getOrder(orderId, user.username.toLong())
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping
    fun getOrders(
        @AuthenticationPrincipal user: UserDetails,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<OrderResponse>>> {
        val response = orderService.getOrders(user.username.toLong(), pageable)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PatchMapping("/{orderId}/cancel")
    fun cancelOrder(
        @PathVariable orderId: Long,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val response = orderService.cancelOrder(orderId, user.username.toLong())
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
