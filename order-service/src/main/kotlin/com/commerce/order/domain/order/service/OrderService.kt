package com.commerce.order.domain.order.service

import com.commerce.order.domain.order.dto.CreateOrderRequest
import com.commerce.order.domain.order.dto.OrderResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderService {
    fun createOrder(request: CreateOrderRequest, userId: Long): OrderResponse
    fun getOrder(orderId: Long, userId: Long): OrderResponse
    fun getOrders(userId: Long, pageable: Pageable): Page<OrderResponse>
    fun cancelOrder(orderId: Long, userId: Long): OrderResponse

    // Saga 보상 트랜잭션 핸들러
    fun handlePaymentCompleted(orderId: Long)
    fun handlePaymentFailed(orderId: Long)
    fun handleStockDeducted(orderId: Long)
    fun handleStockFailed(orderId: Long)
}
