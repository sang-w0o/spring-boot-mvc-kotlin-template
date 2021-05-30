package com.template.auth.controller

import com.template.auth.dto.AccessTokenUpdateRequestDto
import com.template.auth.dto.AccessTokenUpdateResponseDto
import com.template.auth.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class AuthApiController(
    private val authService: AuthService
) {

    @PostMapping("/v1/auth/update-token")
    fun updateAccessToken(@Valid @RequestBody requestDto: AccessTokenUpdateRequestDto): AccessTokenUpdateResponseDto {
        return authService.updateAccessToken(requestDto)
    }
}