package com.template.common.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ErrorResponseDto(
    @field:JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss",
        locale = "Asia/Seoul"
    )
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val remote: String?
)