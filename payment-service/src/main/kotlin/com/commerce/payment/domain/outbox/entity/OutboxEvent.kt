package com.commerce.payment.domain.outbox.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "outbox_events",
    indexes = [Index(name = "idx_outbox_published_created", columnList = "published, created_at")]
)
class OutboxEvent(
    @Column(nullable = false, length = 100) val eventType: String,
    @Column(nullable = false, length = 255) val aggregateId: String,
    @Column(nullable = false, length = 255) val topic: String,
    @Column(nullable = false, columnDefinition = "TEXT") val payload: String
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false)
    var published: Boolean = false
        private set

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column
    var publishedAt: LocalDateTime? = null
        private set

    fun markAsPublished() {
        published = true
        publishedAt = LocalDateTime.now()
    }
}
