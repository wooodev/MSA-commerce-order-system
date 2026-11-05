package com.commerce.product.domain.product.repository

import com.commerce.product.domain.product.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
    fun findAllByDeletedAtIsNull(pageable: Pageable): Page<Product>
}
