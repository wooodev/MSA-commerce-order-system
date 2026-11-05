package com.commerce.notification.domain.notification.entity

import com.commerce.notification.domain.notification.enums.NotificationStatus
import com.commerce.notification.domain.notification.enums.NotificationType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(
    name = "notifications",
    indexes = [
        Index(name = "idx_notifications_user_id", columnList = "user_id"),
        Index(name = "idx_notifications_user_created", columnList = "user_id, created_at")
    ]
)
@EntityListeners(AuditingEntityListener::class)
class Notification(
    @Column(nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val type: NotificationType,

    @Column(nullable = false, length = 255)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var status: NotificationStatus = NotificationStatus.PENDING
        private set

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        private set

    fun markAsSent() { status = NotificationStatus.SENT }
    fun markAsFailed() { status = NotificationStatus.FAILED }
}
