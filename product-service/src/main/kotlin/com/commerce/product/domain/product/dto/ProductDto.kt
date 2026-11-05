package com.commerce.product.domain.product.dto

import com.commerce.product.domain.product.entity.Product
import com.commerce.product.domain.product.entity.Stock
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

// ───────── Request ─────────

data class CreateProductRequest(
    @field:NotBlank(message = "상품명은 필수입니다")
    val name: String,

    val description: String? = null,

    @field:DecimalMin(value = "0.01", message = "가격은 0보다 커야 합니다")
    @field:NotNull
    val price: BigDecimal,

    @field:Min(value = 0, message = "초기 재고는 0 이상이어야 합니다")
    val initialStock: Int = 0
)

data class UpdateStockRequest(
    @field:Min(value = 1, message = "수량은 1 이상이어야 합니다")
    val quantity: Int
)

// ───────── Response ─────────

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockQuantity: Int?,
    val createdAt: LocalDateTime
)

data class StockResponse(
    val productId: Long,
    val quantity: Int
)

// ───────── 변환 확장 함수 ─────────

fun Product.toResponse(stock: Stock? = null): ProductResponse = ProductResponse(
    id = id,
    name = name,
    description = description,
    price = price,
    stockQuantity = stock?.quantity,
    createdAt = createdAt
)

fun Stock.toResponse(): StockResponse = StockResponse(productId = productId, quantity = quantity)
