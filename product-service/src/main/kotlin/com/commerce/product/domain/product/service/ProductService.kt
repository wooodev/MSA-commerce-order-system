package com.commerce.product.domain.product.service

import com.commerce.product.domain.product.dto.CreateProductRequest
import com.commerce.product.domain.product.dto.ProductResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductService {
    fun createProduct(request: CreateProductRequest): ProductResponse
    fun getProduct(productId: Long): ProductResponse
    fun getProducts(pageable: Pageable): Page<ProductResponse>
}
