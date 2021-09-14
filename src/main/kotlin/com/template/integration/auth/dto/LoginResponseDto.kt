package com.template.integration.auth.dto

data class LoginResponseDto(
    val accessToken: String = "",
    val refreshToken: String = ""
)
