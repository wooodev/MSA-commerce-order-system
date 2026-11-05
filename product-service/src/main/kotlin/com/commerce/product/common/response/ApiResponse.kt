package com.commerce.product.common.response

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ErrorResponse?
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(success = true, data = data, error = null)
        fun <T> success(): ApiResponse<T> = ApiResponse(success = true, data = null, error = null)
        fun <T> error(code: String, message: String): ApiResponse<T> =
            ApiResponse(success = false, data = null, error = ErrorResponse(code, message))
    }
}

data class ErrorResponse(val code: String, val message: String)
