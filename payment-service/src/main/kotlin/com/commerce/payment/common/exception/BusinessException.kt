package com.commerce.payment.common.exception

open class BusinessException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)

enum class ErrorCode(val code: String, val message: String) {
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다"),
    DUPLICATE_PAYMENT("DUPLICATE_PAYMENT", "이미 결제가 존재합니다"),
    INVALID_INPUT("INVALID_INPUT", "올바르지 않은 입력값입니다"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "내부 서버 오류가 발생했습니다"),
}

class PaymentNotFoundException(orderId: Long) : BusinessException(
    ErrorCode.PAYMENT_NOT_FOUND,
    "결제 정보를 찾을 수 없습니다: orderId=$orderId"
)
