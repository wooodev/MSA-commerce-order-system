package com.commerce.product.domain.product.service

import com.commerce.product.common.exception.ProductNotFoundException
import com.commerce.product.domain.product.dto.CreateProductRequest
import com.commerce.product.domain.product.dto.ProductResponse
import com.commerce.product.domain.product.dto.toResponse
import com.commerce.product.domain.product.entity.Product
import com.commerce.product.domain.product.entity.Stock
import com.commerce.product.domain.product.repository.ProductRepository
import com.commerce.product.domain.product.repository.StockRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository
) : ProductService {

    override fun createProduct(request: CreateProductRequest): ProductResponse {
        val product = productRepository.save(
            Product(name = request.name, description = request.description, price = request.price)
        )
        val stock = stockRepository.save(Stock(productId = product.id, quantity = request.initialStock))
        return product.toResponse(stock)
    }

    // Cache-Aside 패턴, TTL 5분 — application-local.yml에 Redis TTL 설정
    @Cacheable(value = ["product"], key = "#productId")
    @Transactional(readOnly = true)
    override fun getProduct(productId: Long): ProductResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw ProductNotFoundException(productId)
        val stock = stockRepository.findByProductId(productId)
        return product.toResponse(stock)
    }

    @Transactional(readOnly = true)
    override fun getProducts(pageable: Pageable): Page<ProductResponse> =
        productRepository.findAll(pageable).map { product ->
            val stock = stockRepository.findByProductId(product.id)
            product.toResponse(stock)
        }

    @CacheEvict(value = ["product"], key = "#productId")
    fun evictProductCache(productId: Long) {
        // 캐시 무효화 — 재고 변경 후 호출
    }
}
