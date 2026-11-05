package com.commerce.payment.common.exception

import com.commerce.payment.common.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(PaymentNotFoundException::class)
    fun handleNotFound(e: PaymentNotFoundException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.errorCode.code, e.message))

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.badRequest().body(ApiResponse.error(e.errorCode.code, e.message))

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unhandled exception", e)
        return ResponseEntity.internalServerError()
            .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.code, "서버 오류가 발생했습니다"))
    }
}
