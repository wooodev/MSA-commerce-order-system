package com.commerce.order.domain.order.service

import com.commerce.order.common.exception.OrderNotFoundException
import com.commerce.order.domain.order.dto.CreateOrderRequest
import com.commerce.order.domain.order.dto.OrderResponse
import com.commerce.order.domain.order.dto.toResponse
import com.commerce.order.domain.order.entity.Order
import com.commerce.order.domain.order.entity.OrderItem
import com.commerce.order.domain.order.repository.OrderRepository
import com.commerce.order.domain.outbox.entity.OutboxEvent
import com.commerce.order.domain.outbox.repository.OutboxEventRepository
import com.commerce.order.infrastructure.kafka.event.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) : OrderService {

    override fun createOrder(request: CreateOrderRequest, userId: Long): OrderResponse {
        val totalAmount = request.items.sumOf { it.price.multiply(it.quantity.toBigDecimal()) }

        val order = Order(userId = userId, totalAmount = totalAmount)
        request.items.forEach { req ->
            order.addItem(
                OrderItem(order = order, productId = req.productId, quantity = req.quantity, price = req.price)
            )
        }
        val savedOrder = orderRepository.save(order)

        // Outbox 저장 (Kafka 직접 발행 금지)
        val payload = OrderCreatedPayload(
            orderId = savedOrder.id,
            userId = savedOrder.userId,
            totalAmount = savedOrder.totalAmount,
            items = savedOrder.items.map { OrderItemPayload(it.productId, it.quantity, it.price) }
        )
        outboxEventRepository.save(
            OutboxEvent(
                eventType = "ORDER_CREATED",
                aggregateId = savedOrder.id.toString(),
                topic = KafkaTopics.ORDER_EVENTS,
                payload = objectMapper.writeValueAsString(payload)
            )
        )

        return savedOrder.toResponse()
    }

    @Transactional(readOnly = true)
    override fun getOrder(orderId: Long, userId: Long): OrderResponse {
        val order = orderRepository.findByIdAndUserIdWithItems(orderId, userId)
            ?: throw OrderNotFoundException(orderId)
        return order.toResponse()
    }

    @Transactional(readOnly = true)
    override fun getOrders(userId: Long, pageable: Pageable): Page<OrderResponse> =
        orderRepository.findAllByUserId(userId, pageable).map { it.toResponse() }

    override fun cancelOrder(orderId: Long, userId: Long): OrderResponse {
        val order = orderRepository.findByIdAndUserId(orderId, userId)
            ?: throw OrderNotFoundException(orderId)

        order.cancel()

        val payload = OrderCancelledPayload(orderId = order.id, userId = order.userId, reason = "사용자 요청")
        outboxEventRepository.save(
            OutboxEvent(
                eventType = "ORDER_CANCELLED",
                aggregateId = order.id.toString(),
                topic = KafkaTopics.ORDER_EVENTS,
                payload = objectMapper.writeValueAsString(payload)
            )
        )

        return orderRepository.save(order).toResponse()
    }

    // ───────── Saga 보상 트랜잭션 ─────────

    override fun handlePaymentCompleted(orderId: Long) {
        val order = orderRepository.findByIdOrNull(orderId) ?: return
        order.completePayment()
        orderRepository.save(order)
    }

    override fun handlePaymentFailed(orderId: Long) {
        // PAYMENT_FAILED → 주문 CANCELLED로 변경 (보상 트랜잭션)
        val order = orderRepository.findByIdOrNull(orderId) ?: return
        order.failPayment()
        orderRepository.save(order)
    }

    override fun handleStockDeducted(orderId: Long) {
        val order = orderRepository.findByIdOrNull(orderId) ?: return
        order.completeStockDeduction()
        orderRepository.save(order)
    }

    override fun handleStockFailed(orderId: Long) {
        // STOCK_FAILED → 주문 CANCELLED 처리 (결제 환불은 payment-service에서 처리)
        val order = orderRepository.findByIdOrNull(orderId) ?: return
        order.failStockDeduction()
        orderRepository.save(order)
    }
}
