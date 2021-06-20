package com.template.auth.dto

data class LoginResponseDto(
    val accessToken: String = "",
    val refreshToken: String = ""
)