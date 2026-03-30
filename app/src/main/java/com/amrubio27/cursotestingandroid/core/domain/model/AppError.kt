package com.amrubio27.cursotestingandroid.core.domain.model

sealed class AppError : Exception() {
    data object NetworkError : AppError()
    data object NotFoundError : AppError()
    data object DataBaseError : AppError()
    data class ValidationError(override val message: String) : AppError()
    data class UnknownError(override val message: String?) : AppError()
}