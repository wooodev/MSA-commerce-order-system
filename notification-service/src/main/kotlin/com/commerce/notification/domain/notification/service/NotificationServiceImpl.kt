package com.commerce.notification.domain.notification.service

import com.commerce.notification.domain.notification.entity.Notification
import com.commerce.notification.domain.notification.enums.NotificationType
import com.commerce.notification.domain.notification.repository.NotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun sendNotification(userId: Long, type: String, title: String, content: String) {
        val notificationType = runCatching { NotificationType.valueOf(type) }
            .getOrDefault(NotificationType.SYSTEM)

        val notification = Notification(
            userId = userId,
            type = notificationType,
            title = title,
            content = content
        )
        notificationRepository.save(notification)

        // 실제 환경에서는 이메일/푸시 발송 후 markAsSent() 호출
        notification.markAsSent()
        log.info("알림 발송 완료: userId=$userId, type=$type, title=$title")
    }
}
