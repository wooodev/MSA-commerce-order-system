package com.commerce.product.domain.product.controller

import com.commerce.product.common.response.ApiResponse
import com.commerce.product.domain.product.dto.CreateProductRequest
import com.commerce.product.domain.product.dto.ProductResponse
import com.commerce.product.domain.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    fun createProduct(
        @RequestBody @Valid request: CreateProductRequest
    ): ResponseEntity<ApiResponse<ProductResponse>> {
        val response = productService.createProduct(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response))
    }

    @GetMapping("/{productId}")
    fun getProduct(
        @PathVariable productId: Long
    ): ResponseEntity<ApiResponse<ProductResponse>> {
        val response = productService.getProduct(productId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping
    fun getProducts(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<ProductResponse>>> {
        val response = productService.getProducts(pageable)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
