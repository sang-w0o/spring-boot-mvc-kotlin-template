package com.template.auth.service

interface AuthCommandHandler {
    fun updateAccessToken(refreshToken: String): String
    fun login(email: String, password: String): Pair<String, String>
}
