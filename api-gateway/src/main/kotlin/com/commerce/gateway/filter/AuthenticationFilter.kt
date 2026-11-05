package com.commerce.gateway.filter

import com.commerce.gateway.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * API Gateway JWT 검증 필터 (WebFlux).
 * 검증 통과 시 X-User-Id 헤더를 추가하여 하위 서비스에 전달.
 * 하위 서비스는 X-User-Id 또는 자체 JWT 검증으로 사용자 식별.
 */
@Component
class AuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : WebFilter {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val publicPaths = listOf(
        "/actuator/",
        "/api/v1/auth/",
        "/api/v1/products"
    )

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()

        if (publicPaths.any { path.startsWith(it) }) {
            return chain.filter(exchange)
        }

        val token = exchange.request.headers.getFirst("Authorization")
            ?.takeIf { it.startsWith("Bearer ") }
            ?.substring(7)

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            log.warn("인증 실패: path=$path")
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            return exchange.response.setComplete()
        }

        val userId = jwtTokenProvider.getUserId(token)

        // 하위 서비스에 userId 전달
        val mutatedRequest = exchange.request.mutate()
            .header("X-User-Id", userId.toString())
            .build()

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
    }
}
