package com.commerce.product.domain.product.entity

import com.commerce.product.common.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import java.math.BigDecimal

@Entity
@Table(
    name = "products",
    indexes = [Index(name = "idx_products_name", columnList = "name")]
)
@SQLRestriction("deleted_at IS NULL")
class Product(
    @Column(nullable = false, length = 255)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false, precision = 19, scale = 2)
    var price: BigDecimal,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column
    var deletedAt: java.time.LocalDateTime? = null
        private set

    fun updateInfo(name: String, description: String?, price: BigDecimal) {
        this.name = name
        this.description = description
        this.price = price
    }

    fun delete() {
        deletedAt = java.time.LocalDateTime.now()
    }
}
