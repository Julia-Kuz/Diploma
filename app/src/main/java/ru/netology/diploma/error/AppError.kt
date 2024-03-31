package ru.netology.diploma.error

@Suppress("unused")
sealed class AppError(code: String) : RuntimeException()
class ApiError(code: String) : AppError(code)
object NetworkError : AppError("error_network")
object UnknownError : AppError("error_unknown")
