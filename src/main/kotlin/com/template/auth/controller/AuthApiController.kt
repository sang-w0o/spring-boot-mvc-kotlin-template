package com.template.auth.controller

import com.template.auth.controller.request.AccessTokenUpdateRequest
import com.template.auth.controller.request.LoginRequest
import com.template.auth.controller.response.AccessTokenUpdateResponse
import com.template.auth.controller.response.LoginResponse
import com.template.auth.service.AuthCommandHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class AuthApiController(
    private val authCommandHandler: AuthCommandHandler
) {

    @PostMapping("/v1/auth/update-token")
    fun updateAccessToken(@Valid @RequestBody request: AccessTokenUpdateRequest): AccessTokenUpdateResponse {
        return AccessTokenUpdateResponse(authCommandHandler.updateAccessToken(request.refreshToken))
    }

    @PostMapping("/v1/auth/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResponse {
        val (email, password) = request
        val result = authCommandHandler.login(email, password)
        return LoginResponse(result.first, result.second)
    }
}
