package com.commerce.notification.domain.notification.enums

enum class NotificationType {
    ORDER, PAYMENT, REFUND, STOCK, SYSTEM
}

enum class NotificationStatus {
    PENDING,  // 발송 대기
    SENT,     // 발송 완료
    FAILED    // 발송 실패
}
