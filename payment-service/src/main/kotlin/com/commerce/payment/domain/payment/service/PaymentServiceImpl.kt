package com.commerce.payment.domain.payment.service

import com.commerce.payment.common.exception.PaymentNotFoundException
import com.commerce.payment.domain.outbox.entity.OutboxEvent
import com.commerce.payment.domain.outbox.repository.OutboxEventRepository
import com.commerce.payment.domain.payment.dto.PaymentResponse
import com.commerce.payment.domain.payment.dto.toResponse
import com.commerce.payment.domain.payment.entity.Payment
import com.commerce.payment.domain.payment.repository.PaymentRepository
import com.commerce.payment.infrastructure.kafka.event.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) : PaymentService {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 결제 처리.
     * 멱등성: 동일 orderId 결제 이미 존재하면 skip.
     * items는 PAYMENT_COMPLETED에 포함하여 product-service가 재고 차감에 활용.
     */
    override fun processPayment(
        orderId: Long,
        userId: Long,
        amount: BigDecimal,
        items: List<OrderItemPayload>
    ) {
        if (paymentRepository.existsByOrderId(orderId)) {
            log.info("결제 이미 처리됨 (멱등성): orderId=$orderId")
            return
        }

        val payment = Payment(orderId = orderId, userId = userId, amount = amount)
        paymentRepository.save(payment)

        // 결제 처리 시뮬레이션 (포트폴리오용 — 항상 성공)
        payment.complete()

        val completedPayload = PaymentCompletedPayload(
            paymentId = payment.id,
            orderId = orderId,
            userId = userId,
            amount = amount,
            items = items  // 재고 차감에 필요한 항목 전달
        )
        outboxEventRepository.save(
            OutboxEvent(
                eventType = "PAYMENT_COMPLETED",
                aggregateId = orderId.toString(),
                topic = KafkaTopics.PAYMENT_EVENTS,
                payload = objectMapper.writeValueAsString(completedPayload)
            )
        )

        // 결제 완료 알림
        outboxEventRepository.save(
            OutboxEvent(
                eventType = "NOTIFY_USER",
                aggregateId = orderId.toString(),
                topic = KafkaTopics.NOTIFICATION_EVENTS,
                payload = objectMapper.writeValueAsString(
                    NotifyUserPayload(
                        userId = userId,
                        type = "PAYMENT",
                        title = "결제 완료",
                        content = "주문 #$orderId 결제가 완료되었습니다. 금액: ${amount}원"
                    )
                )
            )
        )

        log.info("결제 완료: orderId=$orderId, paymentId=${payment.id}")
    }

    /**
     * 환불 처리 — STOCK_FAILED 수신 시 보상 트랜잭션.
     */
    override fun refundPayment(orderId: Long) {
        val payment = paymentRepository.findByOrderId(orderId) ?: run {
            log.warn("환불할 결제 없음: orderId=$orderId")
            return
        }

        payment.refund()

        outboxEventRepository.save(
            OutboxEvent(
                eventType = "NOTIFY_USER",
                aggregateId = orderId.toString(),
                topic = KafkaTopics.NOTIFICATION_EVENTS,
                payload = objectMapper.writeValueAsString(
                    NotifyUserPayload(
                        userId = payment.userId,
                        type = "REFUND",
                        title = "결제 환불",
                        content = "주문 #$orderId 결제가 환불되었습니다."
                    )
                )
            )
        )
        log.info("환불 완료 (보상 트랜잭션): orderId=$orderId")
    }

    @Transactional(readOnly = true)
    override fun getPayment(orderId: Long): PaymentResponse =
        paymentRepository.findByOrderId(orderId)?.toResponse()
            ?: throw PaymentNotFoundException(orderId)
}
