package com.template.common.dto

data class ErrorResponseDto(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val remote: String?
)