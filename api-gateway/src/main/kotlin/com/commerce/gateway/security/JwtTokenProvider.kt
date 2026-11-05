package com.commerce.gateway.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(@Value("\${jwt.secret}") secretKey: String) {

    private val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray(Charsets.UTF_8))

    fun validateToken(token: String): Boolean =
        runCatching { Jwts.parser().verifyWith(key).build().parseSignedClaims(token) }.isSuccess

    fun getUserId(token: String): Long =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload.subject.toLong()
}
