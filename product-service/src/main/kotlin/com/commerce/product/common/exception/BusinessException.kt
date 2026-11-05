package com.commerce.product.common.exception

open class BusinessException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)

enum class ErrorCode(val code: String, val message: String) {
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다"),
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "재고가 부족합니다"),
    STOCK_LOCK_ACQUISITION_FAILED("STOCK_LOCK_ACQUISITION_FAILED", "재고 락 획득에 실패했습니다"),
    INVALID_INPUT("INVALID_INPUT", "올바르지 않은 입력값입니다"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "내부 서버 오류가 발생했습니다"),
}

class ProductNotFoundException(val productId: Long) : BusinessException(
    ErrorCode.PRODUCT_NOT_FOUND,
    "상품을 찾을 수 없습니다: productId=$productId"
)

class InsufficientStockException(
    val productId: Long,
    requested: Int,
    available: Int
) : BusinessException(
    ErrorCode.INSUFFICIENT_STOCK,
    "재고 부족 - productId=$productId, 요청=$requested, 현재=$available"
)

class StockLockAcquisitionException(productId: Long) : BusinessException(
    ErrorCode.STOCK_LOCK_ACQUISITION_FAILED,
    "재고 락 획득 실패: productId=$productId"
)
