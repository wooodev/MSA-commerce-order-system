package com.commerce.order.domain.order.enums

enum class OrderStatus {
    PENDING,            // 주문 생성됨
    CONFIRMED,          // 주문 확인됨
    PAYMENT_REQUESTED,  // 결제 요청됨
    PAYMENT_COMPLETED,  // 결제 완료
    PAYMENT_FAILED,     // 결제 실패
    STOCK_DEDUCTED,     // 재고 차감 완료 (= COMPLETED)
    STOCK_FAILED,       // 재고 차감 실패
    COMPLETED,          // 주문 완료
    CANCELLED           // 주문 취소
}
