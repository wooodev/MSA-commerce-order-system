package com.commerce.order.domain.order.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreateOrderRequest(
    @field:NotEmpty(message = "주문 항목은 비어있을 수 없습니다")
    @field:Valid
    val items: List<CreateOrderItemRequest>
)

data class CreateOrderItemRequest(
    @field:NotNull(message = "상품 ID는 필수입니다")
    val productId: Long,

    @field:Min(value = 1, message = "수량은 1 이상이어야 합니다")
    val quantity: Int,

    @field:DecimalMin(value = "0.01", message = "가격은 0보다 커야 합니다")
    val price: BigDecimal
)
