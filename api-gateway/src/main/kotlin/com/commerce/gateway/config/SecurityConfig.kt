package com.commerce.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

/**
 * Gateway Security Config.
 * 실제 인증/인가는 AuthenticationFilter(WebFilter)가 처리.
 * Spring Security는 기본 설정만 적용(csrf/httpBasic 비활성화).
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges.anyExchange().permitAll() // 인증은 AuthenticationFilter에 위임
            }
            .build()
    }
}
