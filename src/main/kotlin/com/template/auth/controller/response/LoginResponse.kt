package com.template.auth.controller.response

data class LoginResponse(
    val accessToken: String = "",
    val refreshToken: String = ""
)
