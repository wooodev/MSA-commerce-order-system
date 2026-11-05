package com.commerce.order.common.exception

import com.commerce.order.common.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(OrderNotFoundException::class)
    fun handleNotFound(e: OrderNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(e.errorCode.code, e.message))
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(e.errorCode.code, e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val message = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "입력값을 확인해주세요"
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ErrorCode.INVALID_INPUT.code, message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unhandled exception", e)
        return ResponseEntity.internalServerError()
            .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.code, "서버 오류가 발생했습니다"))
    }
}
