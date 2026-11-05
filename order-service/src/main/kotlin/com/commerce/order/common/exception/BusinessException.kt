package com.commerce.order.common.exception

open class BusinessException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)

enum class ErrorCode(val code: String, val message: String) {
    // Order
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다"),
    INVALID_ORDER_STATUS("INVALID_ORDER_STATUS", "올바르지 않은 주문 상태입니다"),
    ORDER_CANCEL_NOT_ALLOWED("ORDER_CANCEL_NOT_ALLOWED", "취소할 수 없는 주문 상태입니다"),
    // Common
    INVALID_INPUT("INVALID_INPUT", "올바르지 않은 입력값입니다"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "내부 서버 오류가 발생했습니다"),
}

class OrderNotFoundException(orderId: Long) : BusinessException(
    ErrorCode.ORDER_NOT_FOUND,
    "주문을 찾을 수 없습니다: orderId=$orderId"
)

class InvalidOrderStatusException(message: String) : BusinessException(
    ErrorCode.INVALID_ORDER_STATUS,
    message
)
